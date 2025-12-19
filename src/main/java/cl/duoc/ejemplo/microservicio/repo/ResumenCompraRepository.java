package cl.duoc.ejemplo.microservicio.repo;

import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumenCompraRepository extends JpaRepository<ResumenCompra, Long> {

    Optional<ResumenCompra> findByNumeroResumen(Long numeroResumen);

}
