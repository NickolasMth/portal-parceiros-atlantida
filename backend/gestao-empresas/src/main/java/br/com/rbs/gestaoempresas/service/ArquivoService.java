package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.ArquivoBaixadoDTO;
import br.com.rbs.gestaoempresas.dto.ArquivoResponseDTO;
import br.com.rbs.gestaoempresas.entity.Arquivo;
import br.com.rbs.gestaoempresas.entity.Patrocinio;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.ArquivoRepository;
import br.com.rbs.gestaoempresas.repository.HistoricoRepository;
import br.com.rbs.gestaoempresas.repository.PatrocinioRepository;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class ArquivoService {

    private final ArquivoRepository arquivoRepository;
    private final HistoricoService historico;
    private final UsuarioRepository usuarioRepository;
    private final PatrocinioRepository patrocinioRepository;
    private final ArmazenamentoArquivos armazenamento;
    private final Tika tika = new Tika();

    private final long tamanhoMaximo;

    public ArquivoService(
            ArquivoRepository arquivoRepository,
            HistoricoRepository historicoRepository, HistoricoService historico,
            UsuarioRepository usuarioRepository,
            PatrocinioRepository patrocinioRepository,
            ArmazenamentoArquivos armazenamento,
            @Value("${app.storage.max-file-size:20971520}") long tamanhoMaximo
    ) {
        this.arquivoRepository = arquivoRepository;
        this.historico = historico;
        this.usuarioRepository = usuarioRepository;
        this.patrocinioRepository = patrocinioRepository;
        this.armazenamento = armazenamento;
        this.tamanhoMaximo = tamanhoMaximo;
    }

    @Transactional
    public ArquivoResponseDTO enviar(
            Long patrocinioId,
            MultipartFile arquivoRecebido,
            String categoria,
            Authentication authentication,
            HttpServletRequest request
    ) {

        Usuario usuario = buscarUsuarioAutenticado(authentication);

        Patrocinio patrocinio = patrocinioRepository.findById(patrocinioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patrocínio não encontrado"
                ));

        verificarAcessoPatrocinio(usuario, patrocinio, authentication);

        validarArquivo(arquivoRecebido);

        byte[] conteudo;

        try {
            conteudo = arquivoRecebido.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível ler o arquivo"
            );
        }

        String mimeType = tika.detect(conteudo, arquivoRecebido.getOriginalFilename());

        if ("application/octet-stream".equals(mimeType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível identificar o tipo do arquivo"
            );
        }

        String storageKey = UUID.randomUUID().toString();

        String checksum = calcularChecksum(conteudo);

        boolean armazenado = false;

        try {
            armazenamento.salvar(
                    storageKey,
                    new ByteArrayInputStream(conteudo),
                    conteudo.length
            );

            armazenado = true;

            Arquivo novoArquivo = new Arquivo();

            novoArquivo.setStorageKey(storageKey);
            novoArquivo.setNomeOriginal(arquivoRecebido.getOriginalFilename());
            novoArquivo.setMimeType(mimeType);
            novoArquivo.setTamanhoBytes((long) conteudo.length);
            novoArquivo.setChecksum(checksum);
            novoArquivo.setCategoria(categoria);
            novoArquivo.setPatrocinio(patrocinio);
            novoArquivo.setEnviadoPor(usuario);
            novoArquivo.setMetadados(Map.of(
                    "nomeOriginal", arquivoRecebido.getOriginalFilename()
            ));

            Arquivo salvo = arquivoRepository.save(novoArquivo);

            historico.registrarArquivoHistorico(
                    "UPLOAD",
                    salvo,
                    usuario,
                    patrocinio,
                    request,
                    "Arquivo enviado: " + salvo.getNomeOriginal()
            );

            return converterDTO(salvo);

        } catch (RuntimeException e) {

            if (armazenado) {
                try {
                    armazenamento.excluir(storageKey);
                } catch (RuntimeException erroExclusao) {
                    e.addSuppressed(erroExclusao);
                }
            }

            throw e;
        }
    }

    @Transactional
    public ArquivoBaixadoDTO baixar(
            Long arquivoId,
            Authentication authentication,
            HttpServletRequest request
    ) {

        Usuario usuario = buscarUsuarioAutenticado(authentication);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Arquivo não encontrado"
                ));

        verificarAcessoArquivo(usuario, arquivo, authentication);

        try (var inputStream = armazenamento.baixar(arquivo.getStorageKey())) {

            byte[] conteudo = inputStream.readAllBytes();

            historico.registrarArquivoHistorico(
                    "DOWNLOAD",
                    arquivo,
                    usuario,
                    arquivo.getPatrocinio(),
                    request,
                    "Arquivo baixado: " + arquivo.getNomeOriginal()
            );

            // Retorna os bytes junto com o nome original e o mimeType salvos no banco
            return new ArquivoBaixadoDTO(
                    conteudo,
                    arquivo.getNomeOriginal(),
                    arquivo.getMimeType()
            );

        } catch (IOException | RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível baixar o arquivo"
            );
        }
    }

    @Transactional
    public void excluir(
            Long arquivoId,
            Authentication authentication,
            HttpServletRequest request
    ) {

        Usuario usuario = buscarUsuarioAutenticado(authentication);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Arquivo não encontrado"
                ));

        verificarAcessoArquivo(usuario, arquivo, authentication);

        // Registra o histórico antes de excluir o registro.
        historico.registrarArquivoHistorico(
                "EXCLUSAO",
                arquivo,
                usuario,
                arquivo.getPatrocinio(),
                request,
                "Arquivo excluído: " + arquivo.getNomeOriginal()
        );

        armazenamento.excluir(arquivo.getStorageKey());

        arquivoRepository.delete(arquivo);
    }

    private void validarArquivo(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Envie um arquivo"
            );
        }

        if (arquivo.getSize() > tamanhoMaximo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O arquivo ultrapassa o tamanho máximo permitido"
            );
        }

        if (arquivo.getOriginalFilename() == null ||
                arquivo.getOriginalFilename().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome do arquivo inválido"
            );
        }
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Usuário não autenticado"
            );
        }

        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado"
                ));
    }

    private void verificarAcessoPatrocinio(
            Usuario usuario,
            Patrocinio patrocinio,
            Authentication authentication
    ) {

        if (isAdminRbs(authentication)) {
            return;
        }

        if (usuario.getPatrocinador() == null ||
                !usuario.getPatrocinador().getId()
                        .equals(patrocinio.getPatrocinador().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem acesso a este patrocínio"
            );
        }
    }

    private void verificarAcessoArquivo(
            Usuario usuario,
            Arquivo arquivo,
            Authentication authentication
    ) {

        if (isAdminRbs(authentication)) {
            return;
        }

        if (arquivo.getPatrocinio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem acesso a arquivos gerais da RBS"
            );
        }

        verificarAcessoPatrocinio(
                usuario,
                arquivo.getPatrocinio(),
                authentication
        );
    }

    private boolean isAdminRbs(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN_RBS")
                );
    }

    private String calcularChecksum(byte[] conteudo) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(digest.digest(conteudo));

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Não foi possível calcular o checksum", e
            );
        }
    }

    public ArquivoResponseDTO buscarInfoPorId(
            Long arquivoId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Arquivo não encontrado"
                ));

        verificarAcessoArquivo(usuario, arquivo, authentication);

        return converterDTO(arquivo);
    }


    private ArquivoResponseDTO converterDTO(Arquivo arquivo) {

        return new ArquivoResponseDTO(
                arquivo.getId(),
                arquivo.getNomeOriginal(),
                arquivo.getMimeType(),
                arquivo.getTamanhoBytes(),
                arquivo.getCategoria(),
                arquivo.getPatrocinio() != null
                        ? arquivo.getPatrocinio().getId() : null,
                arquivo.getEnviadoPor() != null
                        ? arquivo.getEnviadoPor().getId() : null,
                arquivo.getCreatedAt()
        );
    }
}