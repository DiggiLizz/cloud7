package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.EstadisticasTicketsDto;
import cl.duoc.ejemplo.microservicio.model.Tickets;
import cl.duoc.ejemplo.microservicio.service.TicketsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketsController {

    private final TicketsService ticketsService;

    public TicketsController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    /**
     * Generar ticket
     */
    @PostMapping
    public ResponseEntity<Tickets> crearTicket(@RequestBody Tickets ticket) {
        Tickets creado = ticketsService.crearTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Obtener ticket por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tickets> obtenerPorId(@PathVariable Long id) {
        return ticketsService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar tickets por usuario
     * Ejemplo: GET /tickets?usuarioId=10
     */
    @GetMapping(params = "usuarioId")
    public ResponseEntity<List<Tickets>> listarPorUsuario(@RequestParam Long usuarioId) {
        List<Tickets> tickets = ticketsService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Listar tickets por evento
     * Ejemplo: GET /tickets?eventoId=5
     */
    @GetMapping(params = "eventoId")
    public ResponseEntity<List<Tickets>> listarPorEvento(@RequestParam("eventoId") Long eventoId) {
        List<Tickets> tickets = ticketsService.buscarPorEvento(eventoId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Modificar detalles del ticket
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tickets> actualizarTicket(@PathVariable Long id,
                                                    @RequestBody Tickets cambios) {
        Tickets actualizado = ticketsService.actualizarTicket(id, cambios);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar ticket
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        boolean eliminado = ticketsService.eliminarTicket(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Estadísticas de ventas / reservas por evento
     * Ejemplo: GET /tickets/estadisticas?eventoId=5
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasTicketsDto> estadisticas(@RequestParam Long eventoId) {
        EstadisticasTicketsDto dto = ticketsService.calcularEstadisticasPorEvento(eventoId);
        return ResponseEntity.ok(dto);
    }
}
