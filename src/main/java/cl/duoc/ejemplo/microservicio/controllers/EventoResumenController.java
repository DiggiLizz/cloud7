package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ResumenEventoResponse;
import cl.duoc.ejemplo.microservicio.service.ResumenEventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos")
public class EventoResumenController {

    private final ResumenEventoService resumenEventoService;

    public EventoResumenController(ResumenEventoService resumenEventoService) {
        this.resumenEventoService = resumenEventoService;
    }

    @GetMapping("/{eventoId}/resumen")
    public ResponseEntity<ResumenEventoResponse> resumenPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(resumenEventoService.obtenerResumen(eventoId));
    }
}
