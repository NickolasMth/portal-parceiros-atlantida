package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento,Long> {
}
