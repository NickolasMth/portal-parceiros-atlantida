package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusEmailLiberado {
    PENDENTE("Email está pendente"),
    ACEITO ("Email está aceito"),
    EXPIRADO("Email está expirado"),
    REVOGADO("Email está revogado");

    private final String descricao;

    StatusEmailLiberado(String descricao) {
        this.descricao = descricao;
    }
}
