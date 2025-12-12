package cl.duoc.ejemplo.microservicio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Si se borra el evento, se borran sus compras
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Evento evento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal precioTotal;

    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    public Compra() {}

    // Contructor
    public Compra(Evento evento, Integer cantidad, BigDecimal precioUnitario, BigDecimal precioTotal, LocalDateTime fechaCompra) {
        this.evento = evento;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        this.fechaCompra = fechaCompra;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }
    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
