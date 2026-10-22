package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Cota;

import org.springframework.data.jpa.repository.*;


public interface CotaRepository extends JpaRepository<Cota, Long> {
}
