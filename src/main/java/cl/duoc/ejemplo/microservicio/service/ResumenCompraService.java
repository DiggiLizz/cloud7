package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraRequest;
import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import cl.duoc.ejemplo.microservicio.repo.ResumenCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ResumenCompraService {

    private final S3Client s3Client;
    private final ResumenCompraRepository resumenRepo;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public String generarYSubirResumen(ResumenCompraRequest compra) {

        String contenido = construirContenidoResumen(compra);
        String key = compra.getCompraId() + "/resumen_compra_" + compra.getCompraId() + ".txt";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromBytes(contenido.getBytes(StandardCharsets.UTF_8))
        );

        ResumenCompra resumen = new ResumenCompra();
        resumen.setNumeroResumen(compra.getCompraId());
        resumen.setS3Key(key);

        resumenRepo.save(resumen);

        return key;
    }

    public byte[] descargarResumen(Long compraId) {

        ResumenCompra resumen = resumenRepo.findByNumeroResumen(compraId)
                .orElseThrow(() -> new IllegalArgumentException("No existe resumen para la compra " + compraId));

        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(resumen.getS3Key())
                        .build()
        ).asByteArray();
    }

    public void actualizarResumen(Long compraId, String nuevoContenido) {

        ResumenCompra resumen = resumenRepo.findByNumeroResumen(compraId)
                .orElseThrow(() -> new IllegalArgumentException("No existe resumen para la compra " + compraId));

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(resumen.getS3Key())
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromBytes(nuevoContenido.getBytes(StandardCharsets.UTF_8))
        );
    }

    public void borrarResumen(Long compraId) {

        ResumenCompra resumen = resumenRepo.findByNumeroResumen(compraId)
                .orElseThrow(() -> new IllegalArgumentException("No existe resumen para la compra " + compraId));

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(resumen.getS3Key())
                        .build()
        );

        resumenRepo.delete(resumen);
    }

    private String construirContenidoResumen(ResumenCompraRequest compra) {

        return """
                RESUMEN DE COMPRA
                -------------------------
                ID Compra: %d
                Evento: %s
                Fecha Evento: %s
                Cantidad: %d
                Precio Unitario: %s
                Precio Total: %s
                Fecha Compra: %s
                """.formatted(
                compra.getCompraId(),
                compra.getNombreEvento(),
                compra.getFechaEvento(),
                compra.getCantidad(),
                compra.getPrecioUnitario(),
                compra.getPrecioTotal(),
                compra.getFechaCompra()
        );
    }
}
