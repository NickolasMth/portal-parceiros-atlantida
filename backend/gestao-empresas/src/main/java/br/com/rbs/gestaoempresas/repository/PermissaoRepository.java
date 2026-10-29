package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.entity.Permissao;
import org.springframework.data.jpa.repository.*;


public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
}
