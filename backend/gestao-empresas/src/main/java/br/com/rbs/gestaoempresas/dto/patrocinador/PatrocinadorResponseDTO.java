package br.com.rbs.gestaoempresas.dto.patrocinador;

import com.fasterxml.jackson.databind.JsonNode;
import br.com.rbs.gestaoempresas.entity.Patrocinador;

import java.time.OffsetDateTime;

public record PatrocinadorResponseDTO(
        Long id,
        String nomeFantasia,
        String razaoSocial,
        String cnpj,
        String emailContato,
        String telefone,
        String website,
        JsonNode endereco,
        Boolean ativo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static PatrocinadorResponseDTO fromEntity(Patrocinador patrocinador) {
        return new PatrocinadorResponseDTO(
                patrocinador.getId(),
                patrocinador.getNomeFantasia(),
                patrocinador.getRazaoSocial(),
                patrocinador.getCnpj(),
                patrocinador.getEmailContato(),
                patrocinador.getTelefone(),
                patrocinador.getWebsite(),
                patrocinador.getEndereco(),
                patrocinador.getAtivo(),
                patrocinador.getCreatedAt(),
                patrocinador.getUpdatedAt()
        );
    }
}