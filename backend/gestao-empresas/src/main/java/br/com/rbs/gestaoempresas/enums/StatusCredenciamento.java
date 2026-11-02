package br.com.rbs.gestaoempresas.enums;

public enum StatusCredenciamento {
    PENDENTE("Credenciamento pendente"),
    EMITIDO("Credenciamento emitido"),
    CANCELADO("Credenciamento cancelado");

    private final String descricao;

    StatusCredenciamento(String descricao) {
        this.descricao = descricao;
    }
}
