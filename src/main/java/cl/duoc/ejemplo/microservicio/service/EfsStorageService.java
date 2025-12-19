package cl.duoc.ejemplo.microservicio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class EfsStorageService {

    @Value("${efs.path}")
    private String efsBasePath;

    public String guardarComprobante(String nombreArchivo, String contenido) {
        try {
            Path dir = Path.of(efsBasePath, "comprobantes");
            Files.createDirectories(dir);

            String safeName = nombreArchivo.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path file = dir.resolve(safeName);

            Files.writeString(file, contenido);
            return file.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando en EFS: " + e.getMessage(), e);
        }
    }
}
