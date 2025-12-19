package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.CompraRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.model.TicketCompra;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import cl.duoc.ejemplo.microservicio.repo.TicketCompraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CompraService {

        private final EventoRepository eventoRepo;
        private final TicketCompraRepository compraRepo;
        private final EfsStorageService efs;
        private final AwsS3Service s3;

        public CompraService(EventoRepository eventoRepo,
                                TicketCompraRepository compraRepo,
                                EfsStorageService efs,
                                AwsS3Service s3) {
                this.eventoRepo = eventoRepo;
                this.compraRepo = compraRepo;
                this.efs = efs;
                this.s3 = s3;
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

                // Comprobante simple (texto)
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
                String efsPath = efs.guardarComprobante("compra_" + compra.getId() + ".txt", comprobante);

                // Subir a S3
                String s3Key = "comprobantes/compra_" + compra.getId() + ".txt";
                s3.subirTexto(s3Key, comprobante);

                compra.setEfsPath(efsPath);
                compra.setS3Key(s3Key);
                compraRepo.save(compra);

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
