package cl.duoc.ejemplo.microservicio.dto;

import java.time.LocalDateTime;

public class BffResumenRequest {

    private Long numeroResumen;      // Ej: compraId
    private String nombreArchivo;    // Ej: compra_2.txt
    private String carpetaResumen;   // Ej: comprobantes
    private String s3Key;            // Ej: comprobantes/compra_2.txt
    private LocalDateTime fechaRegistro;

    public BffResumenRequest() {}

    public Long getNumeroResumen() {
        return numeroResumen;
    }
    public void setNumeroResumen(Long numeroResumen) {
        this.numeroResumen = numeroResumen;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getCarpetaResumen() {
        return carpetaResumen;
    }
    public void setCarpetaResumen(String carpetaResumen) {
        this.carpetaResumen = carpetaResumen;
    }

    public String getS3Key() {
        return s3Key;
    }
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}