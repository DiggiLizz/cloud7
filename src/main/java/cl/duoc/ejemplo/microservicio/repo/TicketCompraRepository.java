package cl.duoc.ejemplo.microservicio.repo;

import cl.duoc.ejemplo.microservicio.model.TicketCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketCompraRepository extends JpaRepository<TicketCompra, Long> {

    @Query("""
        SELECT
            COALESCE(SUM(tc.cantidad), 0),
            COALESCE(SUM(tc.total), 0),
            COUNT(tc)
        FROM TicketCompra tc
        WHERE tc.eventoId = :eventoId
    """)
    Object[] obtenerResumenPorEvento(@Param("eventoId") Long eventoId);
}