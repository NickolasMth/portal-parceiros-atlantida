package br.com.rbs.gestaoempresas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.rbs.gestaoempresas.entity.Patrocinador;

@Repository
public interface PatrocinadorRepository extends JpaRepository<Patrocinador, Long> {
}
