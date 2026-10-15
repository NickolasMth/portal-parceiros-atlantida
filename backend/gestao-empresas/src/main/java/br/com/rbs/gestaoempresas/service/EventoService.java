package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.evento.EventoRequestDTO;
import br.com.rbs.gestaoempresas.dto.evento.EventoResponseDTO;
import br.com.rbs.gestaoempresas.entity.Evento;
import br.com.rbs.gestaoempresas.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository repository;

    @Transactional
    public EventoResponseDTO criarEvento(EventoRequestDTO dto) {

        validarPeriodo(dto);

        Evento evento = Evento.builder()
                .nome(dto.nome())
                .edicao(dto.edicao())
                .descricao(dto.descricao())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .local(dto.local())
                .status(dto.status())
                .build();

        Evento eventoSalvo = repository.save(evento);

        return new EventoResponseDTO(eventoSalvo);
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarEventos() {

        return repository.findAll()
                .stream()
                .map(EventoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {

        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento não encontrado"
                ));

        return new EventoResponseDTO(evento);
    }

    @Transactional
    public EventoResponseDTO editarEvento(
            Long id,
            EventoRequestDTO dto) {

        validarPeriodo(dto);

        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento não encontrado"
                ));

        evento.editarEvento(dto);

        return new EventoResponseDTO(evento);
    }

    @Transactional
    public void excluir(Long id) {

        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento não encontrado"
                ));

        repository.delete(evento);
    }

    private void validarPeriodo(EventoRequestDTO dto) {

        LocalDate dataInicio = dto.dataInicio();
        LocalDate dataFim = dto.dataFim();

        if (dataInicio != null
                && dataFim != null
                && dataFim.isBefore(dataInicio)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à data inicial"
            );
        }
    }
}
