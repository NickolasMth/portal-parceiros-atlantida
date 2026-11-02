package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum TipoPergunta {
    TEXTO("Perguntas do tipo texto escrito"),
    MULTIPLA_ESCOLHA("Pergunta multipla escolha"),
    ESCALA("Pergunta do tipo escala"),
    SIM_NAO("Pergunta do tipo sim ou não");

    private final String descricao;

    TipoPergunta(String descricao) {
        this.descricao = descricao;
    }
}
