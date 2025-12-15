package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import cl.duoc.ejemplo.microservicio.repo.ResumenCompraRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import cl.duoc.ejemplo.microservicio.config.RabbitConfig;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class ResumenCompraService {

        private final S3Client s3Client;
        private final ResumenCompraRepository resumenRepo;
        private final RabbitTemplate rabbitTemplate;

        @Value("${app.s3.bucket}")
        private String bucketName;

    public ResumenCompraService(S3Client s3Client,
        ResumenCompraRepository resumenRepo,
        RabbitTemplate rabbitTemplate) {
            this.s3Client = s3Client;
            this.resumenRepo = resumenRepo;
            this.rabbitTemplate = rabbitTemplate;
    }

    private String construirContenidoResumen(CompraResponse compra) {
        Long idCompra       = compra.getCompraId();           // o getCompraId()
        Long idEvento       = compra.getEventoId();
        String nombreEvento = compra.getNombreEvento();
        String fechaEvento  = compra.getFechaEvento().toString();
        Integer cantidad    = compra.getCantidad();
        String precioUnit   = compra.getPrecioUnitario().toString();
        String precioTotal  = compra.getPrecioTotal().toString();
        String fechaCompra  = compra.getFechaCompra().toString();

        return """
                RESUMEN DE COMPRA
                =================
                ID COMPRA      : %d
                ID EVENTO      : %d
                NOMBRE EVENTO  : %s
                FECHA EVENTO   : %s
                CANTIDAD       : %d
                PRECIO UNITARIO: %s
                PRECIO TOTAL   : %s
                FECHA COMPRA   : %s
                """.formatted(
                idCompra,
                idEvento,
                nombreEvento,
                fechaEvento,
                cantidad,
                precioUnit,
                precioTotal,
                fechaCompra
        );
    }

    /** Nombre estándar del archivo de resumen */
    private String buildFileName(Long numeroResumen) {
        return "resumen_compra_" + numeroResumen + ".txt";
    }

    /** Carpeta = número del resumen (normalmente, id de la compra) */
    private String buildFolder(Long numeroResumen) {
        return String.valueOf(numeroResumen);
    }

    /** Key en S3 = carpeta/archivo */
    private String buildKey(Long numeroResumen) {
        return buildFolder(numeroResumen) + "/" + buildFileName(numeroResumen);
    }

    /**
     * Genera el resumen de compra y lo sube a S3.
     * Devuelve la key generada en el bucket (carpeta/archivo).
     */
    public String generarYSubirResumen(CompraResponse compra) {
        Long numeroResumen = compra.getCompraId();

        String contenido = construirContenidoResumen(compra);
        String key = buildKey(numeroResumen);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(
                putReq,
                RequestBody.fromString(contenido, StandardCharsets.UTF_8)
        );

        // Guardar registro en BD (ya lo teníamos en el paso 3)
        ResumenCompra resumen = new ResumenCompra(
                numeroResumen,
                buildFileName(numeroResumen),
                buildFolder(numeroResumen),
                key,
                LocalDateTime.now()
        );
        resumenRepo.save(resumen);

        // enviar mensaje a la cola 1 (cola-tickets-ok)
        ResumenCompraMessage message = new ResumenCompraMessage(
                numeroResumen,
                key,
                contenido
        );

        rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_OK, message);

        return key;
    }


    /**
     * Descarga el resumen desde S3 como arreglo de bytes.
     * Esto se usará en el endpoint para que el usuario pueda
     * guardar el archivo en su computador.
     */
    public byte[] descargarResumen(Long numeroResumen) {
        ResumenCompra resumen = resumenRepo.findByNumeroResumen(numeroResumen)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe resumen para el número: " + numeroResumen));

        String key = resumen.getS3Key();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );

        return objectBytes.asByteArray();
    }


    //Actualiza el archivo de resumen en S3 con contenido nuevo.
    public void actualizarResumen(Long numeroResumen, String nuevoContenido) {
        ResumenCompra resumen = resumenRepo.findByNumeroResumen(numeroResumen)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe resumen para el número: " + numeroResumen));

        String key = resumen.getS3Key();

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(
                putReq,
                RequestBody.fromString(nuevoContenido, StandardCharsets.UTF_8)
        );
    }

    //Elimina el archivo de resumen desde S3.
    public void borrarResumen(Long numeroResumen) {
        ResumenCompra resumen = resumenRepo.findByNumeroResumen(numeroResumen)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe resumen para el número: " + numeroResumen));

        String key = resumen.getS3Key();

        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(delReq);

        //borrar el registro en BD
        resumenRepo.delete(resumen);
        }
}

