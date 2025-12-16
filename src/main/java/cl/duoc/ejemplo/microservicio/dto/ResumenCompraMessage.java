package cl.duoc.ejemplo.microservicio.dto;

import java.io.Serializable;

/**
 * Mensaje que viaja por RabbitMQ para generar / registrar
 * un resumen de compra asociado a un archivo en S3.
 */
public class ResumenCompraMessage implements Serializable {

    private Long numeroResumen;
    private String s3Key;

    public ResumenCompraMessage() {
    }

    public ResumenCompraMessage(Long numeroResumen, String s3Key) {
        this.numeroResumen = numeroResumen;
        this.s3Key = s3Key;
    }

    public Long getNumeroResumen() {
        return numeroResumen;
    }

    public void setNumeroResumen(Long numeroResumen) {
        this.numeroResumen = numeroResumen;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
}
