
package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.ConviteDtos.AceitarConviteRequest;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.ConviteCriado;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.LiberarEmailRequest;
import br.com.rbs.gestaoempresas.entity.EmailLiberado;
import br.com.rbs.gestaoempresas.service.EmailLiberadoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convites")
public class ConviteController {

    private final EmailLiberadoService service;

    public ConviteController(EmailLiberadoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConviteCriado liberar(
            @Valid @RequestBody LiberarEmailRequest req) {

        return service.liberar(req);
    }

    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aceitar(
            @Valid @RequestBody AceitarConviteRequest req) {

        service.aceitar(req);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ConviteCriado>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
