
package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.ConviteDtos;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.AceitarConviteRequest;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.ConviteCriado;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.LiberarEmailRequest;
import br.com.rbs.gestaoempresas.entity.*;
import br.com.rbs.gestaoempresas.enums.StatusEmailLiberado;
import br.com.rbs.gestaoempresas.enums.StatusUsuario;
import br.com.rbs.gestaoempresas.repository.*;
import br.com.rbs.gestaoempresas.security.UsuarioAutenticado;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
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
                email, StatusEmailLiberado.PENDENTE)) {
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
                convite.getStatusConvite(),
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

        if (!StatusEmailLiberado.PENDENTE.equals(convite.getStatusConvite())) {
            throw new IllegalStateException(
                    "Convite já utilizado ou cancelado");
        }

        Instant agora = Instant.now();

        if (convite.getValidoAte() == null
                || !convite.getValidoAte().isAfter(agora)) {
            convite.setStatusConvite(StatusEmailLiberado.EXPIRADO);
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
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setPerfil(convite.getPerfil());
        usuario.setPatrocinador(convite.getPatrocinador());
        usuario.setTelefone(req.telefone());

        usuarios.save(usuario);

        convite.setStatusConvite(StatusEmailLiberado.ACEITO);
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
                        patrocinadorId, StatusEmailLiberado.PENDENTE);

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

    public List<ConviteCriado> listarTodos() {
        return convites.findAll()
                .stream()
                .map(convite -> new ConviteCriado(
                        convite.getId(),
                        convite.getEmail(),
                        convite.getValidoAte(),
                        convite.getStatusConvite(),
                        convite.getTokenHash()
                ))
                .toList();
    }

    @Transactional
    public void revogarConvite(String emailRequest) {
        String email = emailRequest.trim().toLowerCase(Locale.ROOT);

        EmailLiberado convite = convites
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Convite não encontrado"));

        if (!StatusEmailLiberado.PENDENTE.equals(convite.getStatusConvite())) {
            throw new IllegalStateException(
                    "Convite já utilizado ou cancelado");
        }

        convite.setStatusConvite(StatusEmailLiberado.REVOGADO);
    }

    @Transactional
    public void excluir(String emailRequest) {
        String email = emailRequest.trim().toLowerCase(Locale.ROOT);

        EmailLiberado convite = convites
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Convite não encontrado"));

        if (StatusEmailLiberado.PENDENTE.equals(convite.getStatusConvite())) {
            throw new IllegalStateException(
                    "Revogue ou espere expirar o email antes de exclui-lo");
        }

        convites.delete(convite);
    }
}
