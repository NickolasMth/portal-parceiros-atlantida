package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusAtivacao {
    PLANEJADA("Ativacao está planejada"),
    EM_ANDAMENTO("Ativacao está em andamento"),
    CONCLUIDA("Ativacao está concluida"),
    CANCELADA("Ativacao está cancelada");

    private final String descricao;

    StatusAtivacao(String descricao) {
        this.descricao = descricao;
    }
}
