package br.com.rbs.gestaoempresas.security;

import br.com.rbs.gestaoempresas.repository.PatrocinioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("acesso")
public class AcessoService {

    private final PatrocinioRepository patrocinios;

    public AcessoService(PatrocinioRepository patrocinios) { this.patrocinios = patrocinios; }

    public UsuarioAutenticado atual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof UsuarioAutenticado u)) {
            throw new AccessDeniedException("Não autenticado");
        }
        return u;
    }

    @Transactional(readOnly = true)
    public boolean podeAcessarPatrocinio(Long patrocinioId) {
        UsuarioAutenticado u = atual();
        if (u.isAdminRbs()) return true;
        return u.getPatrocinadorId() != null
            && patrocinios.existsByIdAndPatrocinadorId(patrocinioId, u.getPatrocinadorId());
    }
}
