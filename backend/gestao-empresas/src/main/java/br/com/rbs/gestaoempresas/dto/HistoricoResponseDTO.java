package br.com.rbs.gestaoempresas.dto;

import java.time.Instant;

public record HistoricoResponseDTO(
        Long id,
        String acao,
        String entidade,
        Long entidadeId,
        String descricao,
        Long usuarioId,
        Long patrocinioId,
        Instant createdAt
) {
}