package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusUsuario {
    PENDENTE("Usuario está pendente"),
    ATIVO("Usuario está ativo"),
    INATIVO("Usuario está inativo"),
    BLOQUEADO("Usuario está bloqueado");

    private final String descricao;

    StatusUsuario(String descricao) {
        this.descricao = descricao;
    }
}
