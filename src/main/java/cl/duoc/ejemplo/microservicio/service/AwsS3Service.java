package cl.duoc.ejemplo.microservicio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String defaultBucket;

    public AwsS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /* ======================================================
       LISTAR OBJETOS====================================================== */
    public List<String> listObjects(String bucket) {

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();

        return s3Client.listObjectsV2(request)
                .contents()
                .stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    /* ======================================================
       SUBIR ARCHIVO===================================================== */
    public String upload(String bucket, String key, MultipartFile file) throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return key;
    }

    /* ======================================================
       DESCARGAR ARCHIVO===================================================== */
    public byte[] downloadAsBytes(String bucket, String key) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> response =
                        s3Client.getObject(request)) {

            return response.readAllBytes();

        } catch (IOException e) {
            throw new RuntimeException("Error al descargar archivo desde S3", e);
        }
    }

    /* ======================================================
       MOVER OBJETO (COPY + DELETE) ====================================================== */
    public void moveObject(String bucket, String sourceKey, String destKey) {

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destKey)
                .build();

        s3Client.copyObject(copyRequest);

        deleteObject(bucket, sourceKey);
    }

     // /======================================================ELIMINAR OBJETO======================================================
    public void deleteObject(String bucket, String key) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    //===================================================== MÉTODO ORIGINAL (SE MANTIENE)
    
    public void subirTexto(String key, String contenido) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(defaultBucket)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromString(contenido)
        );
    }
}
