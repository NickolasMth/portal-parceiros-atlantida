package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusEntrega {
    PENDENTE("Entrega está pendente"),
    ENVIADA("Entrega está enviada"),
    APROVADA("Entrega está aprovada"),
    RECUSADA("Entrega está recusada");

    private final String descricao;

    StatusEntrega(String descricao) {
        this.descricao = descricao;
    }
}
