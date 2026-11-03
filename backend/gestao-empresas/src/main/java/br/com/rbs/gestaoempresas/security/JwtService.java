package br.com.rbs.gestaoempresas.security;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Gera o token que o front-end guarda e envia em "Authorization: Bearer ...". */
@Service
public class JwtService {

    public record TokenGerado(String valor, Instant expiraEm) {
    }

    private final JwtEncoder encoder;
    private final long validadeSegundos;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.validade-minutos:60}") long validadeMinutos) {
        this.encoder = encoder;
        this.validadeSegundos = validadeMinutos * 60;
    }

    public TokenGerado gerar(UsuarioAutenticado u) {
        Instant agora = Instant.now();
        Instant expira = agora.plusSeconds(validadeSegundos);

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("portal-patrocinador")
                .subject(u.getUsername())
                .issuedAt(agora)
                .expiresAt(expira)
                .claim("uid", u.getId())
                .claim("auth", u.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).sorted().toList());
        if (u.getPatrocinadorId() != null) {
            claims.claim("pid", u.getPatrocinadorId());
        }

        String token = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims.build())).getTokenValue();
        return new TokenGerado(token, expira);
    }
}
