package br.com.rbs.gestaoempresas.security;

import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Transforma o token já validado em um UsuarioAutenticado, o MESMO tipo que o AcessoService
 * e os @PreAuthorize já usam. Não consulta o banco: tudo vem das claims do token.
 */
@Component
public class JwtParaUsuarioConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        List<String> nomes = jwt.getClaimAsStringList("auth");
        List<GrantedAuthority> authorities = (nomes == null ? List.<String>of() : nomes).stream()
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();

        Long id = ((Number) jwt.getClaim("uid")).longValue();
        Number pid = jwt.getClaim("pid");

        UsuarioAutenticado usuario = new UsuarioAutenticado(
                id,
                pid == null ? null : pid.longValue(),
                jwt.getSubject(),
                null,
                true,
                authorities);
        return new UsernamePasswordAuthenticationToken(usuario, jwt, authorities);
    }
}
