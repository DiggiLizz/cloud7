package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraRequest;
import cl.duoc.ejemplo.microservicio.service.ResumenCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/resumenes")
@RequiredArgsConstructor
public class ResumenCompraController {

    private final ResumenCompraService resumenCompraService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generarResumen(@Valid @RequestBody ResumenCompraRequest compra) {
        resumenCompraService.generarYSubirResumen(compra);

        String mensaje = "Resumen de compra generado y almacenado en S3 para la compra ID: " + compra.getCompraId();
        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    @GetMapping(value = "/{compraId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> descargarResumen(@PathVariable Long compraId) {
        byte[] contenido = resumenCompraService.descargarResumen(compraId);
        String nombreArchivo = "resumen_compra_" + compraId + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.TEXT_PLAIN)
                .body(contenido);
    }

    @PutMapping(path = "/{compraId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> actualizarResumen(@PathVariable Long compraId,
                                                    @RequestParam("file") MultipartFile nuevoArchivo) throws IOException {

        String nuevoContenido = new String(nuevoArchivo.getBytes(), StandardCharsets.UTF_8);
        resumenCompraService.actualizarResumen(compraId, nuevoContenido);

        String mensaje = "Resumen de compra actualizado correctamente para la compra ID: " + compraId;
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping(value = "/{compraId}")
    public ResponseEntity<Void> borrarResumen(@PathVariable Long compraId) {
        resumenCompraService.borrarResumen(compraId);
        return ResponseEntity.noContent().build();
    }
}
