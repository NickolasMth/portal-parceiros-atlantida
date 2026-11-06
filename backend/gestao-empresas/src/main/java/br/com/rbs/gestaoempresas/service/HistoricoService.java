package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.entity.Arquivo;
import br.com.rbs.gestaoempresas.entity.Historico;
import br.com.rbs.gestaoempresas.entity.Patrocinio;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.HistoricoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class HistoricoService {
    private HistoricoRepository repository;

    void registrarArquivoHistorico(
            String acao,
            Arquivo arquivo,
            Usuario usuario,
            Patrocinio patrocinio,
            HttpServletRequest request,
            String descricao
    ) {

        Historico historico = new Historico();

        historico.setAcao(acao);
        historico.setEntidade("ARQUIVO");
        historico.setEntidadeId(arquivo.getId());
        historico.setUsuario(usuario);
        historico.setPatrocinio(patrocinio);
        historico.setDescricao(descricao);
        historico.setIp(request.getRemoteAddr());

        historico.setDadosNovos(Map.of(
                "nomeOriginal", arquivo.getNomeOriginal(),
                "mimeType", arquivo.getMimeType(),
                "tamanhoBytes", arquivo.getTamanhoBytes(),
                "storageKey", arquivo.getStorageKey()
        ));

        repository.save(historico);
    }
}
