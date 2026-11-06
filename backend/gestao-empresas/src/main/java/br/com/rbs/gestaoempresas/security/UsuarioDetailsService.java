package br.com.rbs.gestaoempresas.security;

import br.com.rbs.gestaoempresas.entity.Permissao;
import br.com.rbs.gestaoempresas.entity.Role;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.enums.StatusUsuario;
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

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {

        Usuario usuario = repo.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado."
                ));

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (usuario.getPerfil() != null) {

            for (Role role : usuario.getPerfil().getRoles()) {

                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role.getCodigo())
                );

                for (Permissao permissao : role.getPermissoes()) {

                    authorities.add(
                            new SimpleGrantedAuthority(permissao.getCodigo())
                    );
                }
            }
        }

        Long patrocinadorId = usuario.getPatrocinador() == null
                ? null
                : usuario.getPatrocinador().getId();

        boolean ativo = StatusUsuario.ATIVO.equals(usuario.getStatus());

        return new UsuarioAutenticado(
                usuario.getId(),
                patrocinadorId,
                usuario.getEmail(),
                usuario.getSenhaHash(),
                ativo,
                authorities
        );
    }
}