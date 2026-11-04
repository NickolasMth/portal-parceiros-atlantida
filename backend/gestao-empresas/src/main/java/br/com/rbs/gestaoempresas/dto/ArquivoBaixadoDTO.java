package br.com.rbs.gestaoempresas.dto;

public record ArquivoBaixadoDTO(
        byte[] conteudo,
        String nomeOriginal,
        String mimeType
) {}