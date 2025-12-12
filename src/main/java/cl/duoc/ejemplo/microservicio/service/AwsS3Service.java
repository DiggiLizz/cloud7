package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ListS3ObjectDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Client s3Client;

    //Listar objetos
    public List<ListS3ObjectDto> listObjects(String bucket) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
                .map(o -> new ListS3ObjectDto(o.key(), o.size(), o.lastModified()))
                .collect(Collectors.toList());
    }

    //Descargar archivo
    public byte[] downloadAsBytes(String bucket, String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest)) {
            return s3Object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error descargando objeto: " + e.getMessage(), e);
        }
    }

    //Subir o sobrescribir
    public String upload(String bucket, String key, MultipartFile file) throws IOException {
        String objectKey = (key != null && !key.isBlank()) ? key : file.getOriginalFilename();
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return objectKey;
    }

    //mover objeto
    public void moveObject(String bucket, String sourceKey, String destKey) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destKey)
                .build();

        s3Client.copyObject(copyReq);
        deleteObject(bucket, sourceKey);
    }

    //Eliminar objeto
    public void deleteObject(String bucket, String key) {
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(delReq);
    }
}
