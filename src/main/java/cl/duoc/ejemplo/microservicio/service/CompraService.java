package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.CompraRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.model.TicketCompra;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import cl.duoc.ejemplo.microservicio.repo.TicketCompraRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CompraService {

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

                // Guardar en EFS
                String nombreArchivo = "compra_" + compra.getId() + ".txt";
                String efsPath = efs.guardarComprobante(nombreArchivo, comprobante);

                // Subir a S3
                String carpetaResumen = "comprobantes";
                String s3Key = carpetaResumen + "/" + nombreArchivo;
                s3.subirTexto(s3Key, comprobante);

                // Actualizar compra con rutas
                compra.setEfsPath(efsPath);
                compra.setS3Key(s3Key);
                compraRepo.save(compra);

                // Publicar mensaje a RabbitMQ (cola OK)
                ResumenCompraMessage msg = new ResumenCompraMessage(
                        compra.getId(),
                        nombreArchivo,
                        carpetaResumen,
                        s3Key,
                        LocalDateTime.now()
                );

                try {
                rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_OK, msg);
                } catch (Exception ex) {
                // Si falla RabbitMQ, enviamos a cola de error para trazabilidad
                rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_ERROR, msg);
                }

                return new CompraResponse(
                        compra.getId(),
                        ev.getId(),
                        ev.getNombre(),
                        ev.getFecha().toString(),
                        compra.getCantidad(),
                        compra.getPrecioUnitario(),
                        compra.getTotal(),
                        s3Key,
                        efsPath,
                        compra.getFechaCompra()
                );
        }
}