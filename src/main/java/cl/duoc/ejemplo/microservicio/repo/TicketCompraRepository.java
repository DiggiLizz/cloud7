package cl.duoc.ejemplo.microservicio.repo;

import cl.duoc.ejemplo.microservicio.model.TicketCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCompraRepository extends JpaRepository<TicketCompra, Long> {
}