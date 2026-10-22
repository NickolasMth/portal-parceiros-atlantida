package br.com.rbs.gestaoempresas.service;

import static br.com.rbs.gestaoempresas.dto.PatrocinadorDtos.*;

import br.com.rbs.gestaoempresas.entity.Patrocinador;
import br.com.rbs.gestaoempresas.repository.PatrocinadorRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PatrocinadorService {

    private final PatrocinadorRepository repo;

    public PatrocinadorService(PatrocinadorRepository repo) { this.repo = repo; }

    @Transactional
    public PatrocinadorResponse criar(PatrocinadorRequest r) {
        if (r.cnpj() != null && repo.existsByCnpj(r.cnpj())) throw new IllegalStateException("CNPJ já cadastrado");
        Patrocinador p = new Patrocinador();
        aplicar(p, r);
        return resposta(repo.save(p));
    }

    @Transactional(readOnly = true)
    public List<PatrocinadorResponse> listar() { return repo.findAll().stream().map(this::resposta).toList(); }

    @Transactional(readOnly = true)
    public PatrocinadorResponse buscar(Long id) { return resposta(obter(id)); }

    @Transactional
    public PatrocinadorResponse atualizar(Long id, PatrocinadorRequest r) {
        Patrocinador p = obter(id);
        if (r.cnpj() != null && !r.cnpj().equals(p.getCnpj()) && repo.existsByCnpj(r.cnpj())) {
            throw new IllegalStateException("CNPJ já cadastrado");
        }
        aplicar(p, r);
        return resposta(p);
    }

    /** Não apagamos: inativar preserva o histórico. */
    @Transactional
    public void inativar(Long id) { obter(id).setAtivo(false); }

    private Patrocinador obter(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Patrocinador não encontrado"));
    }

    private void aplicar(Patrocinador p, PatrocinadorRequest r) {
        p.setNomeFantasia(r.nomeFantasia());
        p.setRazaoSocial(r.razaoSocial());
        p.setCnpj(r.cnpj());
        p.setEmailContato(r.emailContato());
        p.setTelefone(r.telefone());
        p.setWebsite(r.website());
        p.setEndereco(r.endereco());
    }

    private PatrocinadorResponse resposta(Patrocinador p) {
        return new PatrocinadorResponse(p.getId(), p.getNomeFantasia(), p.getRazaoSocial(), p.getCnpj(),
            p.getEmailContato(), p.getTelefone(), p.getWebsite(), p.getEndereco(), p.isAtivo());
    }
}
