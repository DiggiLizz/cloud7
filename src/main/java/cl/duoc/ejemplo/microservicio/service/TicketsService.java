package cl.duoc.ejemplo.microservicio.service;

import org.springframework.stereotype.Service;
import cl.duoc.ejemplo.microservicio.model.Tickets;
import cl.duoc.ejemplo.microservicio.dto.EstadisticasTicketsDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TicketsService {

    private final Map<Long, Tickets> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    public Tickets crearTicket(Tickets ticket) {
        Long id = sequence.getAndIncrement();
        ticket.setId(id);

        if (ticket.getFechaReserva() == null) {
            ticket.setFechaReserva(LocalDateTime.now());
        }
        if (ticket.getEstado() == null) {
            ticket.setEstado("RESERVADO");
        }

        storage.put(id, ticket);
        return ticket;
    }

    public Optional<Tickets> obtenerPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Tickets> buscarPorUsuario(Long usuarioId) {
        return storage.values().stream()
                .filter(t -> t.getUsuarioId() != null && t.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public List<Tickets> buscarPorEvento(Long eventoId) {
        return storage.values().stream()
                .filter(t -> t.getEventoId() != null && t.getEventoId().equals(eventoId))
                .collect(Collectors.toList());
    }

    public Tickets actualizarTicket(Long id, Tickets cambios) {
        Tickets existente = storage.get(id);
        if (existente == null) {
            return null;
        }

        if (cambios.getEventoId() != null) {
            existente.setEventoId(cambios.getEventoId());
        }
        if (cambios.getUsuarioId() != null) {
            existente.setUsuarioId(cambios.getUsuarioId());
        }
        if (cambios.getPrecio() != null) {
            existente.setPrecio(cambios.getPrecio());
        }
        if (cambios.getEstado() != null) {
            existente.setEstado(cambios.getEstado());
        }

        storage.put(id, existente);
        return existente;
    }

    public boolean eliminarTicket(Long id) {
        return storage.remove(id) != null;
    }

    public void actualizarS3Key(Long id, String s3Key) {
        Tickets ticket = storage.get(id);
        if (ticket != null) {
            ticket.setS3Key(s3Key);
        }
    }

    public EstadisticasTicketsDto calcularEstadisticasPorEvento(Long eventoId) {
        List<Tickets> ticketsEvento = buscarPorEvento(eventoId);

        long totalReservas = ticketsEvento.size();
        long totalPagados = ticketsEvento.stream()
                .filter(t -> "PAGADO".equalsIgnoreCase(t.getEstado()))
                .count();
        long totalCancelados = ticketsEvento.stream()
                .filter(t -> "CANCELADO".equalsIgnoreCase(t.getEstado()))
                .count();

        BigDecimal montoTotal = ticketsEvento.stream()
                .map(Tickets::getPrecio)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new EstadisticasTicketsDto(
                eventoId,
                totalReservas,
                totalPagados,
                totalCancelados,
                montoTotal
        );
    }
}
