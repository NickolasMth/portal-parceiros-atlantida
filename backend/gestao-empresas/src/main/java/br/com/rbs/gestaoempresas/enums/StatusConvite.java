package br.com.rbs.gestaoempresas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusConvite {
    PENDENTE("Convite esta pendente."),
    CANCELADO("Convite foi cancelado."),
    EXPIRADO("Convite foi expirado."),
    ACEITO("Convite foi aceito.");

    private String statusDescrito;

}
