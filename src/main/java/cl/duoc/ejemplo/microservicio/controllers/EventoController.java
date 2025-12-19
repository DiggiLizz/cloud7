package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.repo.EventoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;

    public EventoController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    //Listar todos los eventos
    @GetMapping
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    //Obtener evento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEvento(@PathVariable Long id) {
        return eventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Crear evento
    @PostMapping
    public ResponseEntity<Evento> crearEvento(@Valid @RequestBody Evento evento) {

        Evento guardado = eventoRepository.save(evento);

        return ResponseEntity
                .created(URI.create("/eventos/" + guardado.getId()))
                .body(guardado);
    }

    //Actualizar evento (usando la misma entidad)
    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable Long id,
                                                    @Valid @RequestBody Evento datos) {

        return eventoRepository.findById(id)
                .map(evento -> {
                    evento.setNombre(datos.getNombre());
                    evento.setFecha(datos.getFecha());
                    evento.setPrecio(datos.getPrecio());
                    Evento actualizado = eventoRepository.save(evento);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //Eliminar evento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {

        if (!eventoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        eventoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
