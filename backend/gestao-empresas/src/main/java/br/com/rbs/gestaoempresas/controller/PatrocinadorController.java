package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.service.PatrocinadorService;
import br.com.rbs.gestaoempresas.dto.PatrocinadorDtos.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patrocinadores")
@PreAuthorize("hasAuthority('PATROCINADOR_GERENCIAR')") // só o time RBS
public class PatrocinadorController {

    private final PatrocinadorService service;

    public PatrocinadorController(PatrocinadorService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PatrocinadorResponse criar(@Valid @RequestBody PatrocinadorRequest r) { return service.criar(r); }

    @GetMapping
    public List<PatrocinadorResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PatrocinadorResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public PatrocinadorResponse atualizar(@PathVariable Long id, @Valid @RequestBody PatrocinadorRequest r) {
        return service.atualizar(id, r);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) { service.inativar(id); }
}
