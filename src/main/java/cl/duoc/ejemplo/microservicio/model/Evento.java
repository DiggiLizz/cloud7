package cl.duoc.ejemplo.microservicio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    public Evento() {}

    public Evento(String nombre, LocalDate fecha, BigDecimal precio) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
    }

    //getters y setters
    public Long getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public BigDecimal getPrecio() {
        return precio;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
