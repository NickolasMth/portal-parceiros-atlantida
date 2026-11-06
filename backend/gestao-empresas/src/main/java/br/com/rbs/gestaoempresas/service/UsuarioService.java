package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.UsuariosDtos.UsuarioResponse;
import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.enums.StatusUsuario;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private static final String ADMIN_RBS = "ADMIN_RBS";
    private static final String ADMIN_PATROCINADOR = "ADMIN_PATROCINADOR";

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuarios(
            Authentication authentication,
            Pageable pageable
    ) {

        Usuario usuarioLogado = obterUsuarioLogado(authentication);

        String perfil = usuarioLogado.getPerfil().getCodigo();

        Page<Usuario> usuarios;

        if (ADMIN_RBS.equals(perfil)) {

            usuarios = usuarioRepository.findAll(pageable);

        }

        else if (ADMIN_PATROCINADOR.equals(perfil)) {

            Long patrocinadorId = obterPatrocinadorId(usuarioLogado);

            usuarios = usuarioRepository
                    .findByPatrocinadorId(
                            patrocinadorId,
                            pageable
                    );

        } else {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não possui permissão para visualizar usuários."
            );
        }

        return usuarios.map(this::converterParaDTO);
    }


    @Transactional
    public void excluirUsuario(
            Long usuarioId,
            Authentication authentication
    ) {

        Usuario usuarioLogado = obterUsuarioLogado(authentication);

        Usuario usuarioAlvo = buscarUsuarioParaExclusao(
                usuarioId,
                usuarioLogado
        );


        if (usuarioLogado.getId().equals(usuarioAlvo.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode desativar sua própria conta."
            );
        }


        if (StatusUsuario.INATIVO.equals(usuarioAlvo.getStatus())) {
            return;
        }

        usuarioAlvo.setStatus(StatusUsuario.INATIVO);
        usuarioRepository.save(usuarioAlvo);
    }


    private Usuario obterUsuarioLogado(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        return usuarioRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."
                ));
    }


    private Long obterPatrocinadorId(Usuario usuario) {

        if (usuario.getPatrocinador() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não possui uma empresa vinculada."
            );
        }

        return usuario.getPatrocinador().getId();
    }


    private Usuario buscarUsuarioParaExclusao(
            Long usuarioId,
            Usuario usuarioLogado
    ) {

        String perfil = usuarioLogado.getPerfil().getCodigo();

        if (ADMIN_RBS.equals(perfil)) {

            return usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuário não encontrado."
                    ));
        }


        if (ADMIN_PATROCINADOR.equals(perfil)) {

            Long patrocinadorId = obterPatrocinadorId(usuarioLogado);

            return usuarioRepository
                    .findByIdAndPatrocinadorId(
                            usuarioId,
                            patrocinadorId
                    )
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuário não encontrado."
                    ));
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Você não possui permissão para desativar usuários."
        );
    }


    private UsuarioResponse converterParaDTO(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getStatus().name(),
                usuario.getPerfil().getNome(),
                usuario.getPatrocinador() != null
                        ? usuario.getPatrocinador().getId()
                        : null,
                usuario.getUltimoAcesso(),
                usuario.getCreatedAt()

        );
    }
}