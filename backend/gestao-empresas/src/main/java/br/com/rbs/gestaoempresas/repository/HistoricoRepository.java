package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.entity.Historico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    List<Historico> findByPatrocinioIdOrderByCreatedAtDesc(Long patrocinioId);

    List<Historico> findByEntidadeAndEntidadeIdOrderByCreatedAtDesc(
            String entidade,
            Long entidadeId
    );
}