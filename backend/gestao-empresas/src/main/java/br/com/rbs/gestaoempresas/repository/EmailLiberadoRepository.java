package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.EmailLiberado;

import java.util.*;
import org.springframework.data.jpa.repository.*;


public interface EmailLiberadoRepository extends JpaRepository<EmailLiberado, Long> {

    Optional<EmailLiberado> findByTokenHash(String tokenHash);
    boolean existsByEmailIgnoreCaseAndStatusConvite(String email, String statusConvite);
    long countByPatrocinadorIdAndStatusConvite(Long patrocinadorId, String statusConvite);
}
