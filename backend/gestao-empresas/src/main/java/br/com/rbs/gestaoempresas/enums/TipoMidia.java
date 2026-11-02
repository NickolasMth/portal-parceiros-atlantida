package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum TipoMidia {
    ARQUIVO("Mídia é um arquivo"),
    TEXTO("Mídia é um texto"),
    LINK("Mídia é um link");

    private final String descricao;

    TipoMidia(String descricao) {
        this.descricao = descricao;
    }
}
