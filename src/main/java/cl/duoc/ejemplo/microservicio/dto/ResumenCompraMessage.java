package cl.duoc.ejemplo.microservicio.dto;

import java.io.Serializable;

/**
 * Mensaje que se enviará por RabbitMQ cuando se genere un resumen de compra.
 * Contiene la información mínima necesaria para que el consumidor
 * pueda guardar o reprocesar el resumen.
 */
public class ResumenCompraMessage implements Serializable {

    private Long numeroResumen;   // normalmente = id de la compra
    private String s3Key;         // key del archivo en S3
    private String contenido;     // texto del resumen

    public ResumenCompraMessage() {
    }

    // Constructor con todos los campos
    public ResumenCompraMessage(Long numeroResumen, String s3Key, String contenido) {
        this.numeroResumen = numeroResumen;
        this.s3Key = s3Key;
        this.contenido = contenido;
    }

    //Getters
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

    public String getContenido() {
        return contenido;
    }

    //Setter
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}

