package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraRequest;
import cl.duoc.ejemplo.microservicio.service.ResumenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/* 🔹 OpenAPI imports */
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/resumenes")
@RequiredArgsConstructor
public class ResumenCompraController {

    private final ResumenCompraService resumenCompraService;

    // ===============================
    // POST /resumenes
    // ===============================
    @Operation(
        summary = "Generar resumen de compra",
        description = "Genera un resumen de compra, lo almacena en AWS S3 y registra sus metadatos en la base de datos"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resumen generado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generarResumen(@RequestBody ResumenCompraRequest compra) {

        resumenCompraService.generarYSubirResumen(compra);
        String mensaje = "Resumen de compra generado y almacenado en S3 para la compra ID: " + compra.getCompraId();

        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    // ===============================
    // GET /resumenes/{compraId}
    // ===============================
    @Operation(
        summary = "Descargar resumen de compra",
        description = "Descarga desde S3 el archivo de resumen asociado a una compra"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen descargado correctamente"),
        @ApiResponse(responseCode = "404", description = "Resumen no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/{compraId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> descargarResumen(@PathVariable Long compraId) {

        byte[] contenido = resumenCompraService.descargarResumen(compraId);
        String nombreArchivo = "resumen_compra_" + compraId + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.TEXT_PLAIN)
                .body(contenido);
    }

    // ===============================
    // PUT /resumenes/{compraId}
    // ===============================
    @Operation(
        summary = "Actualizar resumen de compra",
        description = "Sobrescribe el contenido del resumen de compra almacenado en AWS S3"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Resumen no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(path = "/{compraId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> actualizarResumen(
            @PathVariable Long compraId,
            @RequestParam("file") MultipartFile nuevoArchivo) throws IOException {

        String nuevoContenido = new String(nuevoArchivo.getBytes(), StandardCharsets.UTF_8);
        resumenCompraService.actualizarResumen(compraId, nuevoContenido);

        String mensaje = "Resumen de compra actualizado correctamente para la compra ID: " + compraId;
        return ResponseEntity.ok(mensaje);
    }

    // ===============================
    // DELETE /resumenes/{compraId}
    // ===============================
    @Operation(
        summary = "Eliminar resumen de compra",
        description = "Elimina el archivo de resumen desde S3 y su registro en la base de datos"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resumen eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Resumen no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{compraId}")
    public ResponseEntity<Void> borrarResumen(@PathVariable Long compraId) {

        resumenCompraService.borrarResumen(compraId);
        return ResponseEntity.noContent().build();
    }
}
