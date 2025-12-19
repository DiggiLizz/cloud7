package cl.duoc.ejemplo.microservicio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET_COMPRA")
public class TicketCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPRA")
    private Long id;

    @Column(name = "EVENTO_ID", nullable = false)
    private Long eventoId;

    @Column(name = "CANTIDAD", nullable = false)
    private int cantidad;

    @Column(name = "PRECIO_UNITARIO", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "TOTAL_COMPRA", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "USUARIO", length = 150)
    private String usuario;   // email o sub del JWT

    @Column(name = "FECHA_COMPRA", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "S3_KEY", length = 300)
    private String s3Key;     // ruta del comprobante en S3

    @Column(name = "EFS_PATH", length = 300)
    private String efsPath;   // ruta del comprobante en EFS

    public TicketCompra() {
        // Constructor vacío requerido por JPA
    }

    public TicketCompra(Long eventoId,
                        int cantidad,
                        BigDecimal precioUnitario,
                        BigDecimal total,
                        String usuario,
                        LocalDateTime fechaCompra) {
        this.eventoId = eventoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.usuario = usuario;
        this.fechaCompra = fechaCompra;
    }

    //GETTERS

    public Long getId() {
        return id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public String getS3Key() {
        return s3Key;
    }

    public String getEfsPath() {
        return efsPath;
    }

    //SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public void setEfsPath(String efsPath) {
        this.efsPath = efsPath;
    }
}
