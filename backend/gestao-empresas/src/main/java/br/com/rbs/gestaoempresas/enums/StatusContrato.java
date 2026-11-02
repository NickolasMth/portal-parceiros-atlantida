package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusContrato {
    RASCUNHO("Contrato em rascunho"),
    ENVIADO("Contrato enviado"),
    ASSINADO("Contrato assinado"),
    VENCIDO("Contrato vencido"),
    CANCELADO("Contrato cancelado");

    private final String descricao;

    StatusContrato(String descricao){
        this.descricao = descricao;
    }
}
