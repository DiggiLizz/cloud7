package cl.duoc.ejemplo.microservicio.dto;

import java.math.BigDecimal;

public class ResumenEventoResponse {

    private Long eventoId;
    private String nombreEvento;
    private String fechaEvento; // lo dejamos String por simplicidad (puede ser LocalDate)
    private Long entradasVendidas;
    private BigDecimal totalRecaudado;
    private Long numeroCompras;

    public ResumenEventoResponse() {}

    public ResumenEventoResponse(Long eventoId,
                                    String nombreEvento,
                                    String fechaEvento,
                                    Long entradasVendidas,
                                    BigDecimal totalRecaudado,
                                    Long numeroCompras) {
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.entradasVendidas = entradasVendidas;
        this.totalRecaudado = totalRecaudado;
        this.numeroCompras = numeroCompras;
    }

    public Long getEventoId() { return eventoId; }
    public String getNombreEvento() { return nombreEvento; }
    public String getFechaEvento() { return fechaEvento; }
    public Long getEntradasVendidas() { return entradasVendidas; }
    public BigDecimal getTotalRecaudado() { return totalRecaudado; }
    public Long getNumeroCompras() { return numeroCompras; }

    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    public void setFechaEvento(String fechaEvento) { this.fechaEvento = fechaEvento; }
    public void setEntradasVendidas(Long entradasVendidas) { this.entradasVendidas = entradasVendidas; }
    public void setTotalRecaudado(BigDecimal totalRecaudado) { this.totalRecaudado = totalRecaudado; }
    public void setNumeroCompras(Long numeroCompras) { this.numeroCompras = numeroCompras; }
}