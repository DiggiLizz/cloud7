package cl.duoc.ejemplo.microservicio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompraResponse {
    private Long compraId;
    private Long eventoId;
    private String nombreEvento;
    private LocalDate fechaEvento;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal precioTotal;
    private LocalDateTime fechaCompra;

    public CompraResponse(Long compraId, Long eventoId, String nombreEvento, LocalDate fechaEvento,
                            Integer cantidad, BigDecimal precioUnitario, BigDecimal precioTotal,
                            LocalDateTime fechaCompra) {
        this.compraId = compraId;
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        this.fechaCompra = fechaCompra;
    }

    public Long getCompraId() { return compraId; }
    public Long getEventoId() { return eventoId; }
    public String getNombreEvento() { return nombreEvento; }
    public LocalDate getFechaEvento() { return fechaEvento; }
    public Integer getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
}
