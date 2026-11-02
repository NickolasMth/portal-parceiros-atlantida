package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum TipoCliente {
    CLIENTE("Cliente"),
    RBS("RBS");

    private final String descricao;

    TipoCliente(String descricao) {
        this.descricao = descricao;
    }
}
