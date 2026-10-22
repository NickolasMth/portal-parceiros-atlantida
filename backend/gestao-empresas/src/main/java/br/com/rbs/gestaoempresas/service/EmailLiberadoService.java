package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.ConviteDtos.*;
import br.com.rbs.gestaoempresas.entity.*;
import br.com.rbs.gestaoempresas.repository.*;
import br.com.rbs.gestaoempresas.security.AcessoService;
import br.com.rbs.gestaoempresas.security.UsuarioAutenticado;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailLiberadoService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration VALIDADE = Duration.ofDays(7);

    private final EmailLiberadoRepository convites;
    private final UsuarioRepository usuarios;
    private final PerfilRepository perfis;
    private final PatrocinadorRepository patrocinadores;
    private final PatrocinioRepository patrocinios;
    private final PasswordEncoder encoder;
    private final AcessoService acesso;

    public EmailLiberadoService(EmailLiberadoRepository convites, UsuarioRepository usuarios, PerfilRepository perfis,
                                PatrocinadorRepository patrocinadores, PatrocinioRepository patrocinios,
                                PasswordEncoder encoder, AcessoService acesso) {
        this.convites = convites;
        this.usuarios = usuarios;
        this.perfis = perfis;
        this.patrocinadores = patrocinadores;
        this.patrocinios = patrocinios;
        this.encoder = encoder;
        this.acesso = acesso;
    }

    @Transactional
    public ConviteCriado liberar(LiberarEmailRequest req) {
        UsuarioAutenticado quem = acesso.atual();
        Long patrocinadorId = req.patrocinadorId();
        String perfilCodigo = req.perfilCodigo();

        if (quem.isAdminRbs()) {
            if (perfilCodigo == null) {
                perfilCodigo = patrocinadorId != null ? "ADMIN_PATROCINADOR" : "ADMIN_RBS";
            }
            if ("ADMIN_PATROCINADOR".equals(perfilCodigo) && patrocinadorId == null) {
                throw new IllegalArgumentException("Informe o patrocinador do convidado");
            }
        } else {
            patrocinadorId = quem.getPatrocinadorId();
            perfilCodigo = "ADMIN_PATROCINADOR";
            validarLimiteDaCota(patrocinadorId);
        }

        String email = req.email().trim().toLowerCase();
        if (usuarios.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException("Já existe usuário com este e-mail");
        }
        if (convites.existsByEmailIgnoreCaseAndStatusConvite(email, "PENDENTE")) {
            throw new IllegalStateException("Já existe convite pendente para este e-mail");
        }

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        EmailLiberado c = new EmailLiberado();
        c.setEmail(email);
        c.setPerfil(perfis.findByCodigo(perfilCodigo)
                .orElseThrow(() -> new IllegalArgumentException("Perfil inválido")));
        if (patrocinadorId != null) {
            c.setPatrocinador(patrocinadores.findById(patrocinadorId)
                    .orElseThrow(() -> new NoSuchElementException("Patrocinador não encontrado")));
        }
        c.setCadastradoPorUsuario(usuarios.getReferenceById(quem.getId()));
        c.setTokenHash(sha256(token));
        c.setValidoAte(Instant.now().plus(VALIDADE));
        convites.save(c);

        return new ConviteCriado(c.getId(), email, c.getValidoAte(), token);
    }

    @Transactional
    public void aceitar(AceitarConviteRequest req) {
        EmailLiberado c = convites.findByTokenHash(sha256(req.token()))
                .orElseThrow(() -> new NoSuchElementException("Convite inválido"));
        if (!"PENDENTE".equals(c.getStatusConvite())) {
            throw new IllegalStateException("Convite já utilizado ou cancelado");
        }
        if (c.getValidoAte() != null && c.getValidoAte().isBefore(Instant.now())) {
            throw new IllegalStateException("Convite expirado");
        }
        if (usuarios.findByEmailIgnoreCase(c.getEmail()).isPresent()) {
            throw new IllegalStateException("Já existe usuário com este e-mail");
        }

        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(c.getEmail());
        u.setSenhaHash(encoder.encode(req.senha()));
        u.setStatus("ATIVO");
        u.setPerfil(c.getPerfil());
        u.setPatrocinador(c.getPatrocinador());
        usuarios.save(u);

        c.setStatusConvite("ACEITO");
        c.setAceitoEm(Instant.now());
        c.setAceitoPorUsuario(u);
    }

    private void validarLimiteDaCota(Long patrocinadorId) {
        Patrocinio p = patrocinios.findFirstByPatrocinadorIdOrderByIdDesc(patrocinadorId)
                .orElseThrow(() -> new IllegalStateException("Patrocinador sem patrocínio"));
        int max = ((Number) p.getCota().getBeneficios().getOrDefault("max_usuarios", 1)).intValue();
        long usados = usuarios.countByPatrocinadorId(patrocinadorId)
                + convites.countByPatrocinadorIdAndStatusConvite(patrocinadorId, "PENDENTE");
        if (usados >= max) {
            throw new IllegalStateException("Limite de usuários da cota atingido (" + max + ")");
        }
    }

    private static String sha256(String texto) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}