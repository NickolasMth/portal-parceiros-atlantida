package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Patrocinio;

import java.util.*;
import org.springframework.data.jpa.repository.*;


public interface PatrocinioRepository extends JpaRepository<Patrocinio, Long> {

    List<Patrocinio> findByPatrocinadorId(Long patrocinadorId);
    boolean existsByIdAndPatrocinadorId(Long id, Long patrocinadorId);
    Optional<Patrocinio> findFirstByPatrocinadorIdOrderByIdDesc(Long patrocinadorId);
}
