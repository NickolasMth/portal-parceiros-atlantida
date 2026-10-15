package br.com.rbs.gestaoempresas.dto.evento;

import br.com.rbs.gestaoempresas.entity.Evento;
import br.com.rbs.gestaoempresas.enums.StatusEvento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EventoRequestDTO(

        @NotBlank(message = "O nome do evento é obrigatório")
        @Size(max = 45, message = "O nome deve ter no máximo 45 caracteres")
        String nome,

        @NotNull(message = "A edição do evento é obrigatória")
        @Positive(message = "A edição deve ser um número positivo")
        Integer edicao,

        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String descricao,

        @NotNull(message = "A data de início é obrigatória")
        LocalDate dataInicio,

        @NotNull(message = "A data de término é obrigatória")
        LocalDate dataFim,

        @NotBlank(message = "O local do evento é obrigatório")
        @Size(max = 200, message = "O local deve ter no máximo 200 caracteres")
        String local,

        @NotNull(message = "O status do evento é obrigatório")
        StatusEvento status

) {
    public EventoRequestDTO(Evento evento) {
        this(
                evento.getNome(),
                evento.getEdicao(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFim(),
                evento.getLocal(),
                evento.getStatus()
        );
    }
}
