package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.entity.CotaModulo;
import br.com.rbs.gestaoempresas.entity.Modulo;
import br.com.rbs.gestaoempresas.entity.Patrocinio;
import br.com.rbs.gestaoempresas.repository.CotaModuloRepository;
import br.com.rbs.gestaoempresas.repository.ModuloRepository;
import br.com.rbs.gestaoempresas.repository.PatrocinioRepository;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatrocinioService {

    public record PatrocinioResumo(Long id, String patrocinador, String evento, Integer edicao, String cota, String status) {}
    public record AbaMenu(String codigo, String nome, int ordem) {}

    private final PatrocinioRepository patrocinios;
    private final CotaModuloRepository cotaModulos;
    private final ModuloRepository modulos;
    private final AcessoService acesso;

    public PatrocinioService(PatrocinioRepository patrocinios, CotaModuloRepository cotaModulos,
                             ModuloRepository modulos, AcessoService acesso) {
        this.patrocinios = patrocinios; this.cotaModulos = cotaModulos; this.modulos = modulos; this.acesso = acesso;
    }

    /** Admin RBS: todos. Cliente: só os do seu patrocinador. */
    @Transactional(readOnly = true)
    public List<PatrocinioResumo> listarDoUsuario() {
        var u = acesso.atual();
        List<Patrocinio> lista = u.isAdminRbs() ? patrocinios.findAll()
            : (u.getPatrocinadorId() == null ? List.of() : patrocinios.findByPatrocinadorId(u.getPatrocinadorId()));
        return lista.stream().map(p -> new PatrocinioResumo(p.getId(), p.getPatrocinador().getNomeFantasia(),
            p.getEvento().getNome(), p.getEvento().getEdicao(), p.getCota().getNome(), p.getStatus())).toList();
    }

    /** As abas do menu = módulos que a COTA libera (tabela cota_modulo). Admin RBS vê todas. */
    @Transactional(readOnly = true)
    public List<AbaMenu> menu(Long patrocinioId) {
        Patrocinio p = patrocinios.findById(patrocinioId).orElseThrow(() -> new NoSuchElementException("Patrocínio não encontrado"));
        List<Modulo> abas = acesso.atual().isAdminRbs()
            ? modulos.findByAtivoTrueOrderByOrdem()
            : cotaModulos.findByCotaIdAndHabilitadoTrue(p.getCota().getId()).stream()
                .map(CotaModulo::getModulo).filter(Modulo::isAtivo)
                .sorted(Comparator.comparing(Modulo::getOrdem)).toList();
        return abas.stream().map(m -> new AbaMenu(m.getCodigo(), m.getNome(), m.getOrdem())).toList();
    }
}
