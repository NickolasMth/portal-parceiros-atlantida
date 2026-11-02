package br.com.rbs.gestaoempresas.enums;

import lombok.Getter;

@Getter
public enum StatusImportacaoComprovacao {
    PROCESSANDO("Importação está em processamento"),
    CONCLUIDA("Importação está concluída"),
    COM_ERROS("Importação tem erros"),
    FALHOU("Importação falhou");

    private final String descricao;

    StatusImportacaoComprovacao(String descricao) {
        this.descricao = descricao;
    }
}
