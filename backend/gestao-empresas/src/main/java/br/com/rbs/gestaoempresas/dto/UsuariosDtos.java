package br.com.rbs.gestaoempresas.dto;


import br.com.rbs.gestaoempresas.entity.Perfil;
import br.com.rbs.gestaoempresas.enums.StatusEmailLiberado;
import br.com.rbs.gestaoempresas.enums.StatusUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

public final class UsuariosDtos {

    private UsuariosDtos() {
    }

    public record UsuarioResponse(
            Long id,
            String nome,
            String email,
            String telefone,
            String status,
            String perfil,
            Long idPatrocinador,
            Instant ultimoAcesso,
            Instant createdAt
    ) {
    }
}

