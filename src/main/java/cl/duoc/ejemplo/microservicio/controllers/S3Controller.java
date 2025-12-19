package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final AwsS3Service awsS3Service;

    /** Listar objetos en un bucket (retorna solo keys como String) */
    @GetMapping("/{bucket}/objects")
    public ResponseEntity<List<String>> listObjects(@PathVariable String bucket) {
        return ResponseEntity.ok(awsS3Service.listObjects(bucket));
    }

    /** Descargar archivo como byte[] */
    @GetMapping("/{bucket}/object")
    public ResponseEntity<byte[]> downloadObject(@PathVariable String bucket,
                                                    @RequestParam String key) {

        byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    /** Subir archivo (S3 directo) */
    @PostMapping(path = "/{bucket}/object", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadObject(@PathVariable String bucket,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam(required = false) String key) {
        try {
            String filename = (key == null || key.isBlank())
                    ? file.getOriginalFilename()
                    : key;

            String savedKey = awsS3Service.upload(bucket, filename, file);
            return ResponseEntity.ok("Archivo subido correctamente con key: " + savedKey);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al subir archivo: " + e.getMessage());
        }
    }

    /**Mover objeto dentro del bucket */
    @PostMapping("/{bucket}/move")
    public ResponseEntity<Void> moveObject(@PathVariable String bucket,
                                            @RequestParam String sourceKey,
                                            @RequestParam String destKey) {
        awsS3Service.moveObject(bucket, sourceKey, destKey);
        return ResponseEntity.ok().build();
    }

    /** Eliminar objeto */
    @DeleteMapping("/{bucket}/object")
    public ResponseEntity<Void> deleteObject(@PathVariable String bucket,
                                                @RequestParam String key) {
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.ok().build();
    }
}
