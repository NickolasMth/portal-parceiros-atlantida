package br.com.rbs.gestaoempresas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class ConviteDtos {
    private ConviteDtos() {}

    public record LiberarEmailRequest(@NotBlank @Email String email, Long patrocinadorId, String perfilCodigo) {}

    public record ConviteCriado(Long id, String email, Instant validoAte, String token) {}
    public record AceitarConviteRequest(@NotBlank String token, @NotBlank @Size(max = 200) String nome,
                                        @NotBlank @Size(min = 8, max = 72) String senha) {}
}
