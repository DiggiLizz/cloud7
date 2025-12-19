package cl.duoc.ejemplo.microservicio.repo;

import cl.duoc.ejemplo.microservicio.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
