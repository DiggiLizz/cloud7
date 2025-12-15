package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Compra;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.repo.CompraRepository;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TiendaService {

    private final EventoRepository eventoRepo;
    private final CompraRepository compraRepo;

    public TiendaService(EventoRepository eventoRepo, CompraRepository compraRepo) {
        this.eventoRepo = eventoRepo;
        this.compraRepo = compraRepo;
    }

    public List<Evento> listarEventos() {
        return eventoRepo.findAll();
    }

    @Transactional
    public Evento crearEvento(NuevoEventoRequest dto) {
        Evento e = new Evento(dto.getNombre(), dto.getFecha(), dto.getPrecio());
        return eventoRepo.save(e);
    }

    public Evento obtenerEventoPorId(Long id) {
        return eventoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe evento con id " + id));
    }


    @Transactional
    public Evento actualizarEvento(Long id, ActualizarEventoRequest dto) {
        Evento e = eventoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + id));
        e.setNombre(dto.getNombre());
        e.setFecha(dto.getFecha());
        e.setPrecio(dto.getPrecio());
        return eventoRepo.save(e);
    }

    @Transactional
    public void eliminarEvento(Long id) {
        if (!eventoRepo.existsById(id)) {
            throw new IllegalArgumentException("Evento no encontrado: " + id);
        }
        eventoRepo.deleteById(id);
    }

    @Transactional
    public CompraResponse comprar(NuevaCompraRequest req) {
        Evento ev = eventoRepo.findById(req.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + req.getEventoId()));

        int cantidad = req.getCantidad();
        BigDecimal unit = ev.getPrecio();
        BigDecimal total = unit.multiply(BigDecimal.valueOf(cantidad));

        Compra compra = new Compra(ev, cantidad, unit, total, LocalDateTime.now());
        Compra saved = compraRepo.save(compra);

        return new CompraResponse(
                saved.getId(),
                ev.getId(),
                ev.getNombre(),
                ev.getFecha(),
                cantidad,
                unit,
                total,
                saved.getFechaCompra()
        );
    }
}
