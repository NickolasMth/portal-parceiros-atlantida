package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Usuario;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);
    long countByPatrocinadorId(Long patrocinadorId);
}
