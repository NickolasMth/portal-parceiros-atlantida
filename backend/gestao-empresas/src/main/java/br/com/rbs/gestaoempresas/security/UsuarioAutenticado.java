package br.com.rbs.gestaoempresas.security;

import java.util.Collection;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UsuarioAutenticado implements UserDetails {
    private final Long id;
    private final Long patrocinadorId; // null = time RBS
    private final String email;
    private final String senhaHash;
    private final boolean ativo;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioAutenticado(Long id, Long patrocinadorId, String email, String senhaHash,
                              boolean ativo, Collection<? extends GrantedAuthority> authorities) {
        this.id = id; this.patrocinadorId = patrocinadorId; this.email = email;
        this.senhaHash = senhaHash; this.ativo = ativo; this.authorities = authorities;
    }

    public boolean isAdminRbs() {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_RBS"));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return senhaHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isEnabled() { return ativo; }
}
