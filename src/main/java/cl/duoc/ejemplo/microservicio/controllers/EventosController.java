package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.service.TiendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 🔹 OpenAPI imports (ESTA ES LA EVIDENCIA)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final TiendaService service;

    public EventosController(TiendaService service) {
        this.service = service;
    }

    // ================================
    // GET /eventos
    // ================================
    @Operation(
        summary = "Listar eventos",
        description = "Obtiene la lista completa de eventos disponibles"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de eventos obtenido correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.ok(service.listarEventos());
    }

    // ================================
    // POST /eventos
    // ================================
    @Operation(
        summary = "Crear evento",
        description = "Crea un nuevo evento con nombre, fecha y precio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Evento> crear(@Valid @RequestBody NuevoEventoRequest dto) {
        return ResponseEntity.ok(service.crearEvento(dto));
    }

    // ================================
    // PUT /eventos/{id}
    // ================================
    @Operation(
        summary = "Actualizar evento",
        description = "Actualiza los datos de un evento existente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEventoRequest dto) {
        return ResponseEntity.ok(service.actualizarEvento(id, dto));
    }

    // ================================
    // DELETE /eventos/{id}
    // ================================
    @Operation(
        summary = "Eliminar evento",
        description = "Elimina un evento por su identificador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    // ================================
    // POST /eventos/compras
    // ================================
    @Operation(
        summary = "Comprar entradas",
        description = "Registra la compra de entradas para un evento"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compra realizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de compra inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/compras")
    public ResponseEntity<CompraResponse> comprar(@Valid @RequestBody NuevaCompraRequest dto) {
        return ResponseEntity.ok(service.comprar(dto));
    }
}
