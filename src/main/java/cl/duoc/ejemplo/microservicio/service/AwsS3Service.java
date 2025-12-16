package cl.duoc.ejemplo.microservicio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Servicio responsable de subir archivos de resumen a S3.
 */
@Service
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    public AwsS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Sube un archivo de texto plano a S3.
     *
     * @param key       ruta/clave en el bucket
     * @param contenido contenido del archivo
     */
    public void subirTexto(String key, String contenido) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromString(contenido)
        );
    }
}
