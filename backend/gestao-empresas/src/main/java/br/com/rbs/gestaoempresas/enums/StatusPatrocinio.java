package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusPatrocinio {
    PROSPECTO("Patrocinio está em prospecto"),
    CONTRATADO("Patrocinio está contratado"),
    ATIVO("Patrocinio está ativo"),
    ENCERRADO("Patrocinio está encerrado"),
    CANCELADO("Patrocinio está cancelado");

    private final String descricao;

    StatusPatrocinio(String descricao) {
        this.descricao = descricao;
    }
}
