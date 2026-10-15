package br.com.rbs.gestaoempresas.dto.patrocinador;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatrocinadorRequestDTO(

        @NotBlank(message = "O nome fantasia é obrigatório")
        @Size(max = 45, message = "O nome fantasia deve ter no máximo 45 caracteres")
        String nomeFantasia,

        @NotBlank(message = "A razão social é obrigatória")
        @Size(max = 200, message = "A razão social deve ter no máximo 200 caracteres")
        String razaoSocial,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(
                regexp = "\\d{14}",
                message = "O CNPJ deve conter exatamente 14 dígitos numéricos"
        )
        String cnpj,

        @NotBlank(message = "O e-mail de contato é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        @Size(max = 200, message = "O e-mail deve ter no máximo 200 caracteres")
        String emailContato,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Size(max = 200, message = "O website deve ter no máximo 200 caracteres")
        String website,

        JsonNode endereco

) { }