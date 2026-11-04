package br.com.rbs.gestaoempresas.dto;

import java.time.Instant;

public record ArquivoResponseDTO(
        Long id,
        String nomeOriginal,
        String mimeType,
        Long tamanhoBytes,
        String categoria,
        Long patrocinioId,
        Long enviadoPorUsuarioId,
        Instant createdAt
) {
}