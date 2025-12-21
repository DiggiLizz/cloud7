package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ResumenEventoResponse;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import cl.duoc.ejemplo.microservicio.repo.TicketCompraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ResumenEventoService {

    private final EventoRepository eventoRepository;
    private final TicketCompraRepository ticketCompraRepository;

    public ResumenEventoService(EventoRepository eventoRepository,
                                TicketCompraRepository ticketCompraRepository) {
        this.eventoRepository = eventoRepository;
        this.ticketCompraRepository = ticketCompraRepository;
    }

    public ResumenEventoResponse obtenerResumen(Long eventoId) {

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento no existe: " + eventoId));

        // ✅ Ahora el repo devuelve Object[] (no ResumenEventoAgg)
        Object[] row = ticketCompraRepository.obtenerResumenPorEvento(eventoId);

        // row[0] = SUM(cantidad)      -> Number
        // row[1] = SUM(total)         -> BigDecimal (normalmente)
        // row[2] = COUNT(compras)     -> Number
        long entradasVendidas = 0L;
        BigDecimal totalRecaudado = BigDecimal.ZERO;
        long numeroCompras = 0L;

        if (row != null && row.length == 3) {
            entradasVendidas = (row[0] == null) ? 0L : ((Number) row[0]).longValue();
            numeroCompras = (row[2] == null) ? 0L : ((Number) row[2]).longValue();

            if (row[1] != null) {
                if (row[1] instanceof BigDecimal) {
                    totalRecaudado = (BigDecimal) row[1];
                } else {
                    totalRecaudado = new BigDecimal(row[1].toString());
                }
            }
        }

        return new ResumenEventoResponse(
                evento.getId(),
                evento.getNombre(),
                String.valueOf(evento.getFecha()),
                entradasVendidas,
                totalRecaudado,
                numeroCompras
        );
    }
}