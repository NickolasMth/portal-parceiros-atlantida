package br.com.rbs.gestaoempresas.controller;

import br.com.rbs.gestaoempresas.dto.evento.EventoRequestDTO;
import br.com.rbs.gestaoempresas.dto.evento.EventoResponseDTO;
import br.com.rbs.gestaoempresas.service.EventoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(
            @Valid @RequestBody EventoRequestDTO dto) {

        EventoResponseDTO evento = eventoService.criarEvento(dto);

        URI location = URI.create("/eventos/" + evento.id());

        return ResponseEntity.created(location).body(evento);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> editarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoRequestDTO dto) {

        return ResponseEntity.ok(
                eventoService.editarEvento(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        eventoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
