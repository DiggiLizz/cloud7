package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.BffResumenRequest;
import cl.duoc.ejemplo.microservicio.service.BffColasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/colas")
public class BffColasController {

    private final BffColasService bffColasService;

    public BffColasController(BffColasService bffColasService) {
        this.bffColasService = bffColasService;
    }

    //  Endpoint PRODUCTOR
    @PostMapping("/resumen/producir")
    public ResponseEntity<String> producir(@RequestBody BffResumenRequest request) {
        bffColasService.producirResumen(request);
        return ResponseEntity.ok("OK: mensaje enviado a cola de resúmenes.");
    }

    // Endpoint CONSUMIDOR (HTTP) - consume 1 mensaje y lo procesa
    @PostMapping("/resumen/consumir")
    public ResponseEntity<String> consumir() {
        String result = bffColasService.consumirYProcesarUno();
        return ResponseEntity.ok(result);
    }
}