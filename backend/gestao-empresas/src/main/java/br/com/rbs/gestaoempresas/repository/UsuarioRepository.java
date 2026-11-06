package br.com.rbs.gestaoempresas.repository;

import br.com.rbs.gestaoempresas.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    long countByPatrocinadorId(Long patrocinadorId);

    Page<Usuario> findByPatrocinadorId(
            Long patrocinadorId,
            Pageable pageable
    );

    Optional<Usuario> findByIdAndPatrocinadorId(
            Long id,
            Long patrocinadorId
    );
}