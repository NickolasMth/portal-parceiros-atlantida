package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.ArquivoBaixadoDTO;
import br.com.rbs.gestaoempresas.dto.ArquivoResponseDTO;
import br.com.rbs.gestaoempresas.service.ArquivoService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ArquivoController {

    private final ArquivoService arquivoService;

    public ArquivoController(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
    }

    @PostMapping(
            value = "/patrocinios/{patrocinioId}/arquivos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ArquivoResponseDTO> enviar(
            @PathVariable Long patrocinioId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String categoria,
            Authentication authentication,
            HttpServletRequest request
    ) {

        ArquivoResponseDTO resposta = arquivoService.enviar(
                patrocinioId,
                arquivo,
                categoria,
                authentication,
                request
        );

        return ResponseEntity.status(201).body(resposta);
    }

    @GetMapping("/arquivos/{id}/download")
    public ResponseEntity<byte[]> baixar(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request
    ) {
        // O service já retorna os bytes, o nome original e o tipo de forma unificada
        ArquivoBaixadoDTO arquivoBaixado = arquivoService.baixar(id, authentication, request);

        // Trata o nome original de forma segura contra caracteres especiais/acentos
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(arquivoBaixado.nomeOriginal(), StandardCharsets.UTF_8)
                .build();

        // Define o MediaType dinamicamente (PDF, PNG, etc.), com fallback seguro
        MediaType contentType = MediaType.parseMediaType(
                arquivoBaixado.mimeType() != null
                        ? arquivoBaixado.mimeType()
                        : MediaType.APPLICATION_OCTET_STREAM_VALUE
        );

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(arquivoBaixado.conteudo());
    }

    @DeleteMapping("/arquivos/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request
    ) {

        arquivoService.excluir(id, authentication, request);

        return ResponseEntity.noContent().build();
    }
}