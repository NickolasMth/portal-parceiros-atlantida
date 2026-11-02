package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusDestinacaoCortesia {
    DESTINADA("Cortesia destinada"),
    UTILIZADA("Cortesia utilizada"),
    CANCELADA("Cortesia cancelada");

    private final String descricao;

    StatusDestinacaoCortesia(String descricao) {
        this.descricao = descricao;
    }
}
