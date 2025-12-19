package cl.duoc.ejemplo.microservicio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompraRequest(
        @NotNull Long eventoId,
        @Min(1) int cantidad
) {}
