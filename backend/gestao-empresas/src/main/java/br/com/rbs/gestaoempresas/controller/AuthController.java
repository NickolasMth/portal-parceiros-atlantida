package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.login.LoginDTO;
import br.com.rbs.gestaoempresas.dto.AuthDtos.LoginResponse;
import br.com.rbs.gestaoempresas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /** Público (liberado no SecurityConfig): devolve o token se e-mail e senha estiverem corretos. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginDTO req, HttpServletRequest http) {
        return service.login(req, http.getRemoteAddr());
    }
}
