package br.com.rbs.gestaoempresas.dto.login;


import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank String email,
        @NotBlank String senha
) {
}
