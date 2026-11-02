package br.com.rbs.gestaoempresas.enums;

public enum FormatoArquivo {
    PDF("Formato PDF"),
    XLSX("Formato XLSX"),
    PPTX("Formato PPTX");

    private final String descricao;

    FormatoArquivo(String descricao){
        this.descricao = descricao;
    }
}
