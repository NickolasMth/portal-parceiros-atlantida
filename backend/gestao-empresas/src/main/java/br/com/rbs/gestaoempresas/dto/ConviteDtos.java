
package br.com.rbs.gestaoempresas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class ConviteDtos {

    private ConviteDtos() {
    }

    public record LiberarEmailRequest(
            @NotBlank(message = "O e-mail é obrigatório")
            @Email(message = "Informe um e-mail válido")
            @Size(max = 200, message = "O e-mail deve ter no máximo 200 caracteres")
            String email,

            Long patrocinadorId,

            @Size(max = 50, message = "O código do perfil é muito longo")
            String perfilCodigo
    ) {
    }

    public record ConviteCriado(
            Long id,
            String email,
            Instant validoAte,
            String token
    ) {
    }

    public record AceitarConviteRequest(
            @NotBlank(message = "O token é obrigatório")
            @Size(max = 200, message = "Token inválido")
            String token,

            @NotBlank(message = "O nome é obrigatório")
            @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
            String nome,

            @NotBlank(message = "A senha é obrigatória")
            @Size(min = 8, max = 72,
                    message = "A senha deve ter entre 8 e 72 caracteres")
            String senha
    ) {
    }

    public record ConviteCriadoEvent(
            String email,
            String token
    ) {
    }

}
