package br.com.rbs.gestaoempresas.enums;

public enum StatusRelatorio {
    PENDENTE("Relatório pendente"),
    GERANDO("Relatório em geração"),
    CONCLUIDO("Relatório concluído"),
    ERRO("Relatório com erro");

    private final String descricao;

    StatusRelatorio(String descricao) {
        this.descricao = descricao;
    }
}
