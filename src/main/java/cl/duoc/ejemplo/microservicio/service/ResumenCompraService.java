package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraRequest;
import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import cl.duoc.ejemplo.microservicio.repo.ResumenCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumenCompraService {

    private final S3Client s3Client;
    private final ResumenCompraRepository resumenRepo;

    @Value("${app.s3.bucket}")
    private String bucketName;

    /**
     * Genera el contenido del resumen, lo sube a S3 y persiste metadatos en BD.
     * Retorna la key (ruta) en S3.
     */
    public String generarYSubirResumen(ResumenCompraRequest compra) {

        // 1) Carpeta lógica dentro del bucket
        String carpeta = "resumenes";

        // 2) Nombre del archivo
        String nombreArchivo = "resumen_compra_" + compra.getCompraId() + ".txt";

        // 3) Key completa en S3 (carpeta + "/" + archivo)
        String key = carpeta + "/" + nombreArchivo;

        // 4) Construir contenido del resumen
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

        // 6) Persistir metadatos en BD (SIN NULOS)
        ResumenCompra resumen = new ResumenCompra();
        resumen.setNumeroResumen(compra.getCompraId());        // unique + not null
        resumen.setNombreArchivo(nombreArchivo);               // not null
        resumen.setCarpetaResumen(carpeta);                    // not null
        resumen.setS3Key(key);                                 // not null
        resumen.setFechaRegistro(LocalDateTime.now());         // not null

        resumenRepo.save(resumen);

        return key;
    }

    /**
     * Descarga el archivo desde S3 utilizando la key almacenada en BD.
     */
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

    /**
     * Sobrescribe el archivo en S3 con el nuevo contenido.
     */
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

        // Si quiere, puede registrar modificación:
        // resumen.setFechaRegistro(LocalDateTime.now());
        // resumenRepo.save(resumen);
    }

    /**
     * Elimina el archivo en S3 y luego borra el registro en BD.
     */
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

    /**
     * Construye el contenido del resumen en texto plano.
     */
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
