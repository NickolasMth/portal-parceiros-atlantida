
package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.ConviteDtos;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.AceitarConviteRequest;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.ConviteCriado;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.LiberarEmailRequest;
import br.com.rbs.gestaoempresas.entity.EmailLiberado;
import br.com.rbs.gestaoempresas.entity.Patrocinador;
import br.com.rbs.gestaoempresas.entity.Patrocinio;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.EmailLiberadoRepository;
import br.com.rbs.gestaoempresas.repository.PatrocinadorRepository;
import br.com.rbs.gestaoempresas.repository.PatrocinioRepository;
import br.com.rbs.gestaoempresas.repository.PerfilRepository;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;
import br.com.rbs.gestaoempresas.security.UsuarioAutenticado;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public EmailLiberadoService(
            EmailLiberadoRepository convites,
            UsuarioRepository usuarios,
            PerfilRepository perfis,
            PatrocinadorRepository patrocinadores,
            PatrocinioRepository patrocinios,
            PasswordEncoder encoder,
            AcessoService acesso,
            ApplicationEventPublisher eventPublisher) {

        this.convites = convites;
        this.usuarios = usuarios;
        this.perfis = perfis;
        this.patrocinadores = patrocinadores;
        this.patrocinios = patrocinios;
        this.encoder = encoder;
        this.acesso = acesso;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConviteCriado liberar(LiberarEmailRequest req) {
        UsuarioAutenticado quem = acesso.atual();

        Long patrocinadorId = req.patrocinadorId();
        String perfilCodigo = req.perfilCodigo();

        if (quem.isAdminRbs()) {
            if (perfilCodigo == null || perfilCodigo.isBlank()) {
                perfilCodigo = patrocinadorId != null
                        ? "ADMIN_PATROCINADOR"
                        : "ADMIN_RBS";
            }

            if (!"ADMIN_RBS".equals(perfilCodigo)
                    && !"ADMIN_PATROCINADOR".equals(perfilCodigo)) {
                throw new IllegalArgumentException(
                        "Perfil não permitido para convite");
            }

            if ("ADMIN_PATROCINADOR".equals(perfilCodigo)
                    && patrocinadorId == null) {
                throw new IllegalArgumentException(
                        "Informe o patrocinador do convidado");
            }
        } else {
            patrocinadorId = quem.getPatrocinadorId();
            perfilCodigo = "ADMIN_PATROCINADOR";

            if (patrocinadorId == null) {
                throw new IllegalStateException(
                        "Administrador sem patrocinador associado");
            }

            validarLimiteDaCota(patrocinadorId);
        }

        String email = req.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarios.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException(
                    "Já existe usuário com este e-mail");
        }

        if (convites.existsByEmailIgnoreCaseAndStatusConvite(
                email, "PENDENTE")) {
            throw new IllegalStateException(
                    "Já existe convite pendente para este e-mail");
        }

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        EmailLiberado convite = new EmailLiberado();
        convite.setEmail(email);

        convite.setPerfil(
                perfis.findByCodigo(perfilCodigo)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Perfil inválido"))
        );

        if (patrocinadorId != null) {
            Patrocinador patrocinador = patrocinadores
                    .findById(patrocinadorId)
                    .orElseThrow(() ->
                            new NoSuchElementException(
                                    "Patrocinador não encontrado"));

            convite.setPatrocinador(patrocinador);
        }

        convite.setCadastradoPorUsuario(
                usuarios.getReferenceById(quem.getId())
        );

        convite.setTokenHash(sha256(token));
        convite.setValidoAte(Instant.now().plus(VALIDADE));

        convites.save(convite);

        eventPublisher.publishEvent(
                new ConviteDtos.ConviteCriadoEvent(email, token)
        );

        return new ConviteCriado(
                convite.getId(),
                email,
                convite.getValidoAte(),
                token
        );
    }

    @Transactional
    public void aceitar(AceitarConviteRequest req) {
        String token = req.token().trim();

        EmailLiberado convite = convites
                .findByTokenHash(sha256(token))
                .orElseThrow(() ->
                        new NoSuchElementException("Convite inválido"));

        if (!"PENDENTE".equals(convite.getStatusConvite())) {
            throw new IllegalStateException(
                    "Convite já utilizado ou cancelado");
        }

        Instant agora = Instant.now();

        if (convite.getValidoAte() == null
                || !convite.getValidoAte().isAfter(agora)) {
            throw new IllegalStateException("Convite expirado");
        }

        if (usuarios.findByEmailIgnoreCase(convite.getEmail())
                .isPresent()) {
            throw new IllegalStateException(
                    "Já existe usuário com este e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(req.nome().trim());
        usuario.setEmail(convite.getEmail());
        usuario.setSenhaHash(encoder.encode(req.senha()));
        usuario.setStatus("ATIVO");
        usuario.setPerfil(convite.getPerfil());
        usuario.setPatrocinador(convite.getPatrocinador());

        usuarios.save(usuario);

        convite.setStatusConvite("ACEITO");
        convite.setAceitoEm(agora);
        convite.setAceitoPorUsuario(usuario);
    }

    private void validarLimiteDaCota(Long patrocinadorId) {
        Patrocinio patrocinio = patrocinios
                .findFirstByPatrocinadorIdOrderByIdDesc(patrocinadorId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Patrocinador sem patrocínio"));

        if (patrocinio.getCota() == null
                || patrocinio.getCota().getBeneficios() == null) {
            throw new IllegalStateException(
                    "Cota do patrocinador não está configurada");
        }

        Object valorMaximo = patrocinio.getCota()
                .getBeneficios()
                .getOrDefault("max_usuarios", 1);

        if (!(valorMaximo instanceof Number numero)) {
            throw new IllegalStateException(
                    "Limite de usuários da cota inválido");
        }

        int max = numero.intValue();

        if (max < 0) {
            throw new IllegalStateException(
                    "Limite de usuários da cota inválido");
        }

        long usuariosAtivosOuCadastrados =
                usuarios.countByPatrocinadorId(patrocinadorId);

        long convitesPendentes =
                convites.countByPatrocinadorIdAndStatusConvite(
                        patrocinadorId, "PENDENTE");

        long usados = usuariosAtivosOuCadastrados + convitesPendentes;

        if (usados >= max) {
            throw new IllegalStateException(
                    "Limite de usuários da cota atingido (" + max + ")");
        }
    }

    private static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    texto.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "Algoritmo SHA-256 indisponível", ex);
        }
    }
}
