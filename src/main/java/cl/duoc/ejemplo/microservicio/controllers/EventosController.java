package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ActualizarEventoRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.dto.NuevaCompraRequest;
import cl.duoc.ejemplo.microservicio.dto.NuevoEventoRequest;
import cl.duoc.ejemplo.microservicio.model.Evento;
import cl.duoc.ejemplo.microservicio.service.TiendaService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final TiendaService service;

    public EventosController(TiendaService service) {
        this.service = service;
    }

    // =========================
    // GET /eventos  (HATEOAS)
    // =========================
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Evento>>> listar() {

        List<EntityModel<Evento>> eventos = service.listarEventos().stream()
                .map(evento -> EntityModel.of(evento,
                        linkTo(methodOn(EventosController.class)
                                .obtenerPorId(evento.getId())).withSelfRel()
                ))
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(eventos,
                        linkTo(methodOn(EventosController.class).listar()).withSelfRel()
                )
        );
    }

    // =========================
    // GET /eventos/{id} (CLAVE PARA HATEOAS)
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Evento>> obtenerPorId(@PathVariable Long id) {

        Evento evento = service.obtenerEventoPorId(id);

        EntityModel<Evento> model = EntityModel.of(evento,
                linkTo(methodOn(EventosController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(EventosController.class).listar()).withRel("eventos"),
                linkTo(methodOn(EventosController.class).comprar(null)).withRel("comprar")
        );

        return ResponseEntity.ok(model);
    }

    // =========================
    // POST /eventos
    // =========================
    @PostMapping
    public ResponseEntity<EntityModel<Evento>> crear(
            @Valid @RequestBody NuevoEventoRequest dto) {

        Evento evento = service.crearEvento(dto);

        EntityModel<Evento> model = EntityModel.of(evento,
                linkTo(methodOn(EventosController.class)
                        .obtenerPorId(evento.getId())).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

    // =========================
    // PUT /eventos/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Evento>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEventoRequest dto) {

        Evento evento = service.actualizarEvento(id, dto);

        EntityModel<Evento> model = EntityModel.of(evento,
                linkTo(methodOn(EventosController.class)
                        .obtenerPorId(id)).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

    // =========================
    // DELETE /eventos/{id}
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // POST /eventos/compras
    // =========================
    @PostMapping("/compras")
    public ResponseEntity<CompraResponse> comprar(
            @Valid @RequestBody NuevaCompraRequest dto) {
        return ResponseEntity.ok(service.comprar(dto));
    }
}
