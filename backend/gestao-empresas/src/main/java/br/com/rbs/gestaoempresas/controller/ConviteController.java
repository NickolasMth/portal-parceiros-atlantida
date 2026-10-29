
package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.ConviteDtos.AceitarConviteRequest;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.ConviteCriado;
import br.com.rbs.gestaoempresas.dto.ConviteDtos.LiberarEmailRequest;
import br.com.rbs.gestaoempresas.service.EmailLiberadoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/convites")
public class ConviteController {

    private final EmailLiberadoService service;

    public ConviteController(EmailLiberadoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('EMAIL_LIBERAR')")
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
}
