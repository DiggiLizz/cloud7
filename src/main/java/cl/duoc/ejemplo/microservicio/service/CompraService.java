package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.CompraRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.model.TicketCompra;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import cl.duoc.ejemplo.microservicio.repo.TicketCompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CompraService {

    private static final Logger log = LoggerFactory.getLogger(CompraService.class);

    private final EventoRepository eventoRepo;
    private final TicketCompraRepository compraRepo;
    private final EfsStorageService efs;
    private final AwsS3Service s3;
    private final RabbitTemplate rabbitTemplate;

    public CompraService(EventoRepository eventoRepo,
                         TicketCompraRepository compraRepo,
                         EfsStorageService efs,
                         AwsS3Service s3,
                         RabbitTemplate rabbitTemplate) {
        this.eventoRepo = eventoRepo;
        this.compraRepo = compraRepo;
        this.efs = efs;
        this.s3 = s3;
        this.rabbitTemplate = rabbitTemplate;
    }

    public CompraResponse comprar(CompraRequest req, String usuario) {

        Evento ev = eventoRepo.findById(req.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento no existe: " + req.eventoId()));

        BigDecimal total = ev.getPrecio().multiply(BigDecimal.valueOf(req.cantidad()));

        TicketCompra compra = new TicketCompra();
        compra.setEventoId(ev.getId());
        compra.setCantidad(req.cantidad());
        compra.setPrecioUnitario(ev.getPrecio());
        compra.setTotal(total);
        compra.setUsuario(usuario);
        compra.setFechaCompra(LocalDateTime.now());

        compra = compraRepo.save(compra);

        String comprobante = """
                COMPROBANTE COMPRA
                CompraId: %d
                Evento: %s
                Fecha evento: %s
                Cantidad: %d
                Precio unitario: %s
                Total: %s
                Usuario: %s
                Fecha compra: %s
                """.formatted(
                compra.getId(),
                ev.getNombre(),
                ev.getFecha(),
                compra.getCantidad(),
                compra.getPrecioUnitario(),
                compra.getTotal(),
                compra.getUsuario(),
                compra.getFechaCompra()
        );

        String nombreArchivo = "compra_" + compra.getId() + ".txt";
        String carpetaResumen = "comprobantes";

        // ✅ Opción A: "best effort" (no bloquear compra por fallas externas)
        String efsPath = null;
        String s3Key = null;

        // 1) Guardar en EFS (si falla, seguimos)
        try {
            efsPath = efs.guardarComprobante(nombreArchivo, comprobante);
        } catch (Exception ex) {
            log.warn("EFS no disponible o sin permisos. CompraId={} archivo={}. Se continúa sin EFS.",
                    compra.getId(), nombreArchivo, ex);
        }

        // 2) Subir a S3 (si falla, seguimos)
        try {
            s3Key = carpetaResumen + "/" + nombreArchivo;
            s3.subirTexto(s3Key, comprobante);
        } catch (Exception ex) {
            // Si falla S3, dejamos s3Key en null para no “mentir” en la respuesta
            log.warn("S3 no autorizado o no disponible. CompraId={} bucketKey={}. Se continúa sin S3.",
                    compra.getId(), s3Key, ex);
            s3Key = null;
        }

        // 3) Actualizar compra con rutas reales (pueden quedar null)
        try {
            compra.setEfsPath(efsPath);
            compra.setS3Key(s3Key);
            compraRepo.save(compra);
        } catch (Exception ex) {
            // Esto no debería fallar, pero si falla, tampoco debe botar la compra ya guardada
            log.warn("No se pudo actualizar rutas EFS/S3 en BD. CompraId={}.", compra.getId(), ex);
        }

        // 4) Publicar mensaje a RabbitMQ (si falla OK, intenta ERROR; si falla todo, no revienta)
        ResumenCompraMessage msg = new ResumenCompraMessage(
                compra.getId(),
                nombreArchivo,
                carpetaResumen,
                s3Key, // puede ser null (y está bien)
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_OK, msg);
        } catch (Exception ex) {
            try {
                rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_ERROR, msg);
            } catch (Exception ignored) {
                log.warn("RabbitMQ no disponible. CompraId={} (no se pudo enviar OK ni ERROR).", compra.getId());
            }
        }

        return new CompraResponse(
                compra.getId(),
                ev.getId(),
                ev.getNombre(),
                ev.getFecha().toString(),
                compra.getCantidad(),
                compra.getPrecioUnitario(),
                compra.getTotal(),
                s3Key,   // puede ser null
                efsPath, // puede ser null
                compra.getFechaCompra()
        );
    }
}