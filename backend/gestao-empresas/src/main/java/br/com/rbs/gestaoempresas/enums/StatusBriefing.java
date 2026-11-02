package br.com.rbs.gestaoempresas.enums;

public enum StatusBriefing {
    RASCUNHO("Briefing em rascunho"),
    ENVIADO("Briefing enviado"),
    RESPONDIDO("Briefing respondido");

    private final String descricao;

    StatusBriefing(String descricao) {
        this.descricao = descricao;
    }
}
