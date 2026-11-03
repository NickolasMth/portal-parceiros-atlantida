package br.com.rbs.gestaoempresas.dto;

import java.time.Instant;
import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record UsuarioLogado(
            Long id,
            String nome,
            String email,
            Long patrocinadorId,
            boolean adminRbs,
            List<String> autoridades
    ) {
    }

    public record LoginResponse(
            String accessToken,
            String tokenType,
            Instant expiraEm,
            UsuarioLogado usuario
    ) {
    }
}
