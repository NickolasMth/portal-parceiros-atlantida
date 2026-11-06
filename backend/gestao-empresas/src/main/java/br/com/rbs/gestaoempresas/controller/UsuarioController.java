package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.UsuariosDtos.UsuarioResponse;
import br.com.rbs.gestaoempresas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        return ResponseEntity.ok(
                usuarioService.listarUsuarios(authentication, pageable)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(
            @PathVariable Long id,
            Authentication authentication
    ) {

        usuarioService.excluirUsuario(id, authentication);

        return ResponseEntity.noContent().build();
    }
}