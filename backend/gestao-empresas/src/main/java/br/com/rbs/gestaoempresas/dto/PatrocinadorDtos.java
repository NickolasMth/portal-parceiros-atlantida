package br.com.rbs.gestaoempresas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public final class PatrocinadorDtos {
    private PatrocinadorDtos() {}

    public record PatrocinadorRequest(
        @NotBlank @Size(max = 120) String nomeFantasia,
        @Size(max = 200) String razaoSocial,
        @Size(max = 20) String cnpj,
        @Email @Size(max = 200) String emailContato,
        @Size(max = 20) String telefone,
        @Size(max = 200) String website,
        Map<String, Object> endereco) {}

    public record PatrocinadorResponse(Long id, String nomeFantasia, String razaoSocial, String cnpj,
                                       String emailContato, String telefone, String website,
                                       Map<String, Object> endereco, boolean ativo) {}
}
