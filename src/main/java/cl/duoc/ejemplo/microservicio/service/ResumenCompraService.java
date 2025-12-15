package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraRequest;
import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import cl.duoc.ejemplo.microservicio.repo.ResumenCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumenCompraService {

    private final S3Client s3Client;
    private final ResumenCompraRepository resumenRepo;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Transactional
    public String generarYSubirResumen(ResumenCompraRequest compra) {

        Long compraId = compra.getCompraId();

        // 1) Definir carpeta (NO NULL en BD)
        String carpeta = "resumenes";

        // 2) Definir nombre de archivo (recomendado persistirlo)
        String nombreArchivo = "resumen_compra_" + compraId + ".txt";

        // 3) Definir key S3 coherente: carpeta/nombreArchivo
        String key = carpeta + "/" + nombreArchivo;

        // 4) Construir contenido
        String contenido = construirContenidoResumen(compra);

        // 5) Subir a S3
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromBytes(contenido.getBytes(StandardCharsets.UTF_8))
        );

        // 6) Guardar/actualizar registro en BD (evita duplicar numero_resumen)
        ResumenCompra resumen = resumenRepo.findByNumeroResumen(compraId)
                .orElseGet(ResumenCompra::new);

        resumen.setNumeroResumen(compraId);
        resumen.setCarpetaResumen(carpeta);          // <-- CLAVE: evita NULL en carpeta_resumen
        resumen.setNombreArchivo(nombreArchivo);      // <-- Recomendado (y suele ser NOT NULL también)
        resumen.setS3Key(key);

        // Si su entidad tiene fechaRegistro NOT NULL, setéela:
        if (resumen.getFechaRegistro() == null) {
            resumen.setFechaRegistro(LocalDateTime.now());
        }

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

    @Transactional
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

        // (opcional) actualizar fechaRegistro si su modelo lo requiere
        if (resumen.getFechaRegistro() != null) {
            resumen.setFechaRegistro(LocalDateTime.now());
            resumenRepo.save(resumen);
        }
    }

    @Transactional
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
