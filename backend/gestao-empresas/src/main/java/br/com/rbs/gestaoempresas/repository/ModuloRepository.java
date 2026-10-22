package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Modulo;

import java.util.*;
import org.springframework.data.jpa.repository.*;


public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findByAtivoTrueOrderByOrdem();
}
