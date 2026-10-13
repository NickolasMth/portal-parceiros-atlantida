package br.com.rbs.gestaoempresas.dto.evento;

import br.com.rbs.gestaoempresas.entity.Evento;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record EventoResponseDTO(
        Long id,
        String nome,
        Integer edicao,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim,
        String local,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public EventoResponseDTO(Evento evento) {
        this(
                evento.getId(),
                evento.getNome(),
                evento.getEdicao(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getLocal(),
                evento.getStatus(),
                evento.getCreatedAt(),
                evento.getUpdatedAt()
        );
    }
}
