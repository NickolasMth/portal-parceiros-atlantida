package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.CotaModulo;

import java.util.*;
import org.springframework.data.jpa.repository.*;


public interface CotaModuloRepository extends JpaRepository<CotaModulo, Long> {

    Optional<CotaModulo> findByCotaIdAndModuloCodigo(Long cotaId, String codigoModulo);
    List<CotaModulo> findByCotaIdAndHabilitadoTrue(Long cotaId);
}
