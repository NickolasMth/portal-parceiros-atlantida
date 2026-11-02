package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusCortesia {
    DISPONIVEL("Cortesia disponivel"),
    ESGOTADA("Cortesia esgotada"),
    CANCELADA("Cortesia cancelada");

    private final String descricao;

    StatusCortesia(String descricao) {
        this.descricao = descricao;
    }
}
