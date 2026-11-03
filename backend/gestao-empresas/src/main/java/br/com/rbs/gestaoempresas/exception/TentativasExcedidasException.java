package br.com.rbs.gestaoempresas.exception;


public class TentativasExcedidasException extends RuntimeException {

    public TentativasExcedidasException() {
        super("Muitas tentativas de login. Tente novamente em alguns minutos.");
    }
}
