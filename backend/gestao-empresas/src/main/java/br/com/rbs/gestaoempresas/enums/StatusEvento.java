package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusEvento {

    PLANEJADO("Evento está sendo planejado"),
    EM_ANDAMENTO("Evento está em andamento"),
    ENCERRADO("Evento já foi encerrado"),
    CANCELADO("Evento foi cancelado");

    private final String descricao;

    StatusEvento(String descricao) {
        this.descricao = descricao;
    }
}
