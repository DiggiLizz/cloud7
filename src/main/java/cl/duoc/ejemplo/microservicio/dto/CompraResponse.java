package cl.duoc.ejemplo.microservicio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResponse(
        Long compraId,
        Long eventoId,
        String nombreEvento,
        String fechaEvento,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal total,
        String s3Key,
        String efsPath,
        LocalDateTime fechaCompra
) {}
