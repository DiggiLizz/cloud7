package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
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
    
    //cola ok
    // PRODUCTOR OK
    @PostMapping("/resumen/producir")
    public ResponseEntity<String> producirOk(@RequestBody BffResumenRequest request) {
        bffColasService.producirResumenOk(request);
        return ResponseEntity.ok("OK: mensaje enviado a " + RabbitConfig.COLA_TICKETS_OK);
    }

    // CONSUMIDOR OK
    @PostMapping("/resumen/consumir")
    public ResponseEntity<String> consumirOk() {
        return ResponseEntity.ok(bffColasService.consumirOkYProcesarUno());
    }

    //cola error
    // PRODUCTOR ERROR
    @PostMapping("/resumen-error/producir")
    public ResponseEntity<String> producirError(@RequestBody BffResumenRequest request) {
        bffColasService.producirResumenError(request);
        return ResponseEntity.ok("OK: mensaje enviado a " + RabbitConfig.COLA_TICKETS_ERROR);
    }

    // CONSUMIDOR ERROR
    @PostMapping("/resumen-error/consumir")
    public ResponseEntity<String> consumirError() {
        return ResponseEntity.ok(bffColasService.consumirErrorYProcesarUno());
    }
}