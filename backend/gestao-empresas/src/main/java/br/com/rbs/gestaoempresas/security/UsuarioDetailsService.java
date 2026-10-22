package br.com.rbs.gestaoempresas.security;


import br.com.rbs.gestaoempresas.entity.Permissao;
import br.com.rbs.gestaoempresas.entity.Role;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) { this.repo = repo; }

    @Override
    @Transactional(readOnly = true) // roles/permissões são LAZY
    public UserDetails loadUserByUsername(String email) {
        Usuario u = repo.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (u.getPerfil() != null) {
            for (Role r : u.getPerfil().getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getCodigo()));
                for (Permissao p : r.getPermissoes()) {
                    authorities.add(new SimpleGrantedAuthority(p.getCodigo()));
                }
            }
        }
        Long patrocinadorId = u.getPatrocinador() == null ? null : u.getPatrocinador().getId();
        return new UsuarioAutenticado(u.getId(), patrocinadorId, u.getEmail(),
            u.getSenhaHash(), "ATIVO".equals(u.getStatus()), authorities);
    }
}
