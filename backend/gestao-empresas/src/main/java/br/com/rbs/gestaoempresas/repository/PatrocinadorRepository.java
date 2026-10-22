package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Patrocinador;


import org.springframework.data.jpa.repository.*;


public interface PatrocinadorRepository extends JpaRepository<Patrocinador, Long> {

    boolean existsByCnpj(String cnpj);
}
