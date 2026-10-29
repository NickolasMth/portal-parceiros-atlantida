package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.service.AcessoService;
import br.com.rbs.gestaoempresas.security.UsuarioAutenticado;
import br.com.rbs.gestaoempresas.service.PatrocinioService;
import br.com.rbs.gestaoempresas.service.PatrocinioService.AbaMenu;
import br.com.rbs.gestaoempresas.service.PatrocinioService.PatrocinioResumo;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MeController {

    public record MeResponse(Long id, String email, Long patrocinadorId, boolean adminRbs, List<String> autoridades) {}

    private final AcessoService acesso;
    private final PatrocinioService patrocinioService;

    public MeController(AcessoService acesso, PatrocinioService patrocinioService) {
        this.acesso = acesso; this.patrocinioService = patrocinioService;
    }

    @GetMapping("/me")
    public MeResponse me() {
        UsuarioAutenticado u = acesso.atual();
        return new MeResponse(u.getId(), u.getUsername(), u.getPatrocinadorId(), u.isAdminRbs(),
            u.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList());
    }

    @GetMapping("/patrocinios")
    public List<PatrocinioResumo> meusPatrocinios() { return patrocinioService.listarDoUsuario(); }

    @GetMapping("/patrocinios/{id}/menu")
    @PreAuthorize("@acesso.podeAcessarPatrocinio(#id)")
    public List<AbaMenu> menu(@PathVariable Long id) { return patrocinioService.menu(id); }
}
