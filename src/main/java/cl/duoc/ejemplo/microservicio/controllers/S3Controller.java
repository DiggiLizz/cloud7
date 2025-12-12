package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ListS3ObjectDto;
import cl.duoc.ejemplo.microservicio.service.AwsS3Service;
import cl.duoc.ejemplo.microservicio.service.EfsService;
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
    private final EfsService efsService;

    /** ✅ Listar objetos en un bucket */
    @GetMapping("/{bucket}/objects")
    public ResponseEntity<List<ListS3ObjectDto>> listObjects(@PathVariable String bucket) {
        List<ListS3ObjectDto> dtoList = awsS3Service.listObjects(bucket);
        return ResponseEntity.ok(dtoList);
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

    /** Subir archivo (guarda en EFS/NFS y luego en S3) */
    @PostMapping(path = "/{bucket}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadObject(@PathVariable String bucket,
                                                @RequestParam(required = false) String key,
                                                @RequestParam("file") MultipartFile file) {
        try {
            String filename = (key == null || key.isBlank()) ? file.getOriginalFilename() : key;

            efsService.saveToEfs(filename, file);                 // Persistencia local (EFS/NFS)
            String savedKey = awsS3Service.upload(bucket, filename, file);  // Subida a S3

            return ResponseEntity.ok("Archivo subido correctamente con key: " + savedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al subir archivo: " + e.getMessage());
        }
    }

    /**  Alias: subir archivo con la ruta /object (mismo manejo de errores) */
    @PostMapping(path = "/{bucket}/object", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAlias(@PathVariable String bucket,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam(required = false) String key) {
        try {
            String filename = (key == null || key.isBlank()) ? file.getOriginalFilename() : key;

            efsService.saveToEfs(filename, file);
            String savedKey = awsS3Service.upload(bucket, filename, file);

            return ResponseEntity.ok("Archivo subido correctamente con key: " + savedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error al subir archivo: " + e.getMessage());
        }
    }

    /** Mover objeto dentro del bucket */
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
