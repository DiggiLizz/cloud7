package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketS3Controller {

    private final AwsS3Service awsS3Service;

    @Value("${app.s3.bucket}")
    private String bucket;

    // DESCARGAR
    @GetMapping("/archivo")
    public ResponseEntity<byte[]> descargar(@RequestParam String key) {
        byte[] bytes = awsS3Service.downloadAsBytes(bucket, key);
        String filename = key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : key;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    // ACTUALIZAR (REEMPLAZA)
    @PutMapping(path = "/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> actualizar(@RequestParam String key,
                                            @RequestParam("file") MultipartFile file) {
        try {
            awsS3Service.upload(bucket, key, file);
            return ResponseEntity.ok("Ticket actualizado. key=" + key);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    // MOVER (COPY + DELETE)
    @PostMapping("/archivo/mover")
    public ResponseEntity<String> mover(@RequestParam String sourceKey,
                                        @RequestParam String destKey) {
        awsS3Service.moveObject(bucket, sourceKey, destKey);
        return ResponseEntity.ok("Ticket movido. sourceKey=" + sourceKey + " destKey=" + destKey);
    }

    // ELIMINAR
    @DeleteMapping("/archivo")
    public ResponseEntity<String> eliminar(@RequestParam String key) {
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.ok("Ticket eliminado. key=" + key);
    }
}