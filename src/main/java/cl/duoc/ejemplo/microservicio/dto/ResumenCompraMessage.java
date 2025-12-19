package cl.duoc.ejemplo.microservicio.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResumenCompraMessage implements Serializable {

    private Long numeroResumen;      // ID de la compra (TicketCompra.id)
    private String nombreArchivo;    // Ej: compra_15.txt
    private String carpetaResumen;   // Ej: compras/15
    private String s3Key;            // Ej: compras/15/compra_15.txt
    private LocalDateTime fechaRegistro;

    public ResumenCompraMessage() {
        // Constructor vacío requerido por Jackson / Rabbit
    }

    public ResumenCompraMessage(Long numeroResumen,
                                String nombreArchivo,
                                String carpetaResumen,
                                String s3Key,
                                LocalDateTime fechaRegistro) {
        this.numeroResumen = numeroResumen;
        this.nombreArchivo = nombreArchivo;
        this.carpetaResumen = carpetaResumen;
        this.s3Key = s3Key;
        this.fechaRegistro = fechaRegistro;
    }

    //GETTERS

    public Long getNumeroResumen() {
        return numeroResumen;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getCarpetaResumen() {
        return carpetaResumen;
    }

    public String getS3Key() {
        return s3Key;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    //SETTERS

    public void setNumeroResumen(Long numeroResumen) {
        this.numeroResumen = numeroResumen;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public void setCarpetaResumen(String carpetaResumen) {
        this.carpetaResumen = carpetaResumen;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
