package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.login.LoginDTO;
import br.com.rbs.gestaoempresas.dto.AuthDtos.LoginResponse;
import br.com.rbs.gestaoempresas.dto.AuthDtos.UsuarioLogado;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;
import br.com.rbs.gestaoempresas.security.JwtService;
import br.com.rbs.gestaoempresas.security.JwtService.TokenGerado;
import br.com.rbs.gestaoempresas.security.LoginThrottle;
import br.com.rbs.gestaoempresas.security.UsuarioAutenticado;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarios;
    private final LoginThrottle throttle;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       UsuarioRepository usuarios, LoginThrottle throttle) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarios = usuarios;
        this.throttle = throttle;
    }

    @Transactional
    public LoginResponse login(LoginDTO req, String ip) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        String chave = email + "|" + ip;

        throttle.verificar(chave);

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.senha()));
        } catch (AuthenticationException e) {
            throttle.falhou(chave);
            throw e;
        }
        throttle.sucesso(chave);

        UsuarioAutenticado u = (UsuarioAutenticado) auth.getPrincipal();
        Usuario usuario = usuarios.findById(u.getId()).orElseThrow();
        usuario.setUltimoAcesso(Instant.now());

        TokenGerado token = jwtService.gerar(u);
        List<String> autoridades = u.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).sorted().toList();

        return new LoginResponse(
                token.valor(),
                "Bearer",
                token.expiraEm(),
                new UsuarioLogado(u.getId(), usuario.getNome(), u.getUsername(),
                        u.getPatrocinadorId(), u.isAdminRbs(), autoridades));
    }
}
