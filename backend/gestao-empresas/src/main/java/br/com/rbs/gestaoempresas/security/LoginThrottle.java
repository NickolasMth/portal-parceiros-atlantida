package br.com.rbs.gestaoempresas.security;

import br.com.rbs.gestaoempresas.exception.TentativasExcedidasException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LoginThrottle {

    private static final int MAX_FALHAS = 5;
    private static final Duration JANELA = Duration.ofMinutes(15);
    private static final int LIMITE_PARA_LIMPEZA = 10_000;

    private record Estado(int falhas, Instant ultimaFalha, Instant bloqueadoAte) {
    }

    private final Map<String, Estado> estados = new ConcurrentHashMap<>();

    public void verificar(String chave) {
        Estado e = estados.get(chave);
        if (e != null && e.bloqueadoAte() != null && e.bloqueadoAte().isAfter(Instant.now())) {
            throw new TentativasExcedidasException();
        }
    }

    public void falhou(String chave) {
        Instant agora = Instant.now();
        estados.merge(chave, new Estado(1, agora, null), (antigo, novo) -> {
            boolean expirou = antigo.ultimaFalha().plus(JANELA).isBefore(agora);
            int falhas = expirou ? 1 : antigo.falhas() + 1;
            return falhas >= MAX_FALHAS
                    ? new Estado(falhas, agora, agora.plus(JANELA))
                    : new Estado(falhas, agora, null);
        });
        if (estados.size() > LIMITE_PARA_LIMPEZA) {
            estados.entrySet().removeIf(en -> en.getValue().ultimaFalha().plus(JANELA).isBefore(agora));
        }
    }

    public void sucesso(String chave) {
        estados.remove(chave);
    }
}
