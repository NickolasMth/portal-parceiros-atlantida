package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.entity.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {

    Optional<Arquivo> findByStorageKey(String storageKey);

    List<Arquivo> findByPatrocinioIdOrderByCreatedAtDesc(Long patrocinioId);

    List<Arquivo> findByPatrocinioIsNullOrderByCreatedAtDesc();
}