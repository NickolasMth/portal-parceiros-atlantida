package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusPesquisa {
    RASCUNHO("Pesquisa em rascunho"),
    ABERTA("Pesquisa aberta"),
    ENCERRADA("Pesquisa encerrada");

    private final String descricao;

    StatusPesquisa(String descricao) {
        this.descricao = descricao;
    }
}
