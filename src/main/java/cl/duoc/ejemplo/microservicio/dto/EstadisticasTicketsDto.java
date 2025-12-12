package cl.duoc.ejemplo.microservicio.dto;

import java.math.BigDecimal;

public class EstadisticasTicketsDto {

    private Long eventoId;
    private long totalReservas;
    private long totalPagados;
    private long totalCancelados;
    private BigDecimal montoTotal;

    public EstadisticasTicketsDto() {
    }

    public EstadisticasTicketsDto(Long eventoId,
                                 long totalReservas,
                                 long totalPagados,
                                 long totalCancelados,
                                 BigDecimal montoTotal) {
        this.eventoId = eventoId;
        this.totalReservas = totalReservas;
        this.totalPagados = totalPagados;
        this.totalCancelados = totalCancelados;
        this.montoTotal = montoTotal;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public long getTotalReservas() {
        return totalReservas;
    }

    public void setTotalReservas(long totalReservas) {
        this.totalReservas = totalReservas;
    }

    public long getTotalPagados() {
        return totalPagados;
    }

    public void setTotalPagados(long totalPagados) {
        this.totalPagados = totalPagados;
    }

    public long getTotalCancelados() {
        return totalCancelados;
    }

    public void setTotalCancelados(long totalCancelados) {
        this.totalCancelados = totalCancelados;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}