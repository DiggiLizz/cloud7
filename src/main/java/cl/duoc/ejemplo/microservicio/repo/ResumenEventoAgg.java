package cl.duoc.ejemplo.microservicio.repo;

import java.math.BigDecimal;

public interface ResumenEventoAgg {
    Long getEntradasVendidas();
    BigDecimal getTotalRecaudado();
    Long getNumeroCompras();
}
