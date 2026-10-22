package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Perfil;

import java.util.*;
import org.springframework.data.jpa.repository.*;


public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByCodigo(String codigo);
}
