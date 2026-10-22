package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.domain.*;
import br.com.rbs.gestaoempresas.entity.Permissao;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
}
