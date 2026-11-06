package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.EmailLiberado;

import java.util.*;

import br.com.rbs.gestaoempresas.enums.StatusEmailLiberado;
import org.springframework.data.jpa.repository.*;


public interface EmailLiberadoRepository extends JpaRepository<EmailLiberado, Long> {

    Optional<EmailLiberado> findByEmailIgnoreCase(String email);
    Optional<EmailLiberado> findByTokenHash(String tokenHash);
    boolean existsByEmailIgnoreCaseAndStatusConvite(String email, StatusEmailLiberado statusConvite);
    long countByPatrocinadorIdAndStatusConvite(Long patrocinadorId, StatusEmailLiberado statusConvite);
}
