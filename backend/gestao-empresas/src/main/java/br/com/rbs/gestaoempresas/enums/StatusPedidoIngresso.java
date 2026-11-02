package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusPedidoIngresso {
    PENDENTE("Pedido está pendente"),
    APROVADO("Pedido está aprovado"),
    PARCIAL("Pedido está parcial"),
    RECUSADO("Pedido está recusado");

    private final String descricao;

    StatusPedidoIngresso(String descricao) {
        this.descricao = descricao;
    }
}
