package cl.duoc.ejemplo.microservicio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESUMEN_COMPRA")
public class ResumenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESUMEN")
    private Long id;

    // Número de resumen. En tu caso será el mismo que el ID de la compra.
    @Column(name = "NUMERO_RESUMEN", nullable = false, unique = true)
    private Long numeroResumen;

    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 200)
    private String nombreArchivo;

    // Carpeta dentro del bucket, normalmente el mismo número de resumen en formato String
    @Column(name = "CARPETA_RESUMEN", nullable = false, length = 100)
    private String carpetaResumen;

    // Key completo de S3: carpeta + "/" + nombreArchivo
    @Column(name = "S3_KEY", nullable = false, length = 300)
    private String s3Key;

    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;

    public ResumenCompra() {
    }

    // Constructor
    public ResumenCompra(Long numeroResumen,
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

    // Getters
    public Long getId() {
        return id;
    }

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

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

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

