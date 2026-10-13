package br.com.rbs.gestaoempresas.entity;

import br.com.rbs.gestaoempresas.dto.evento.EventoRequestDTO;
import br.com.rbs.gestaoempresas.enums.StatusEvento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @NotBlank(message = "O nome do evento é obrigatório")
    @Size(max = 45, message = "O nome deve ter no máximo 45 caracteres")
    @Column(length = 45, nullable = false)
    private String nome;

    @NotNull(message = "A edição do evento é obrigatória")
    @Positive(message = "A edição deve ser um número positivo")
    private Integer edicao;

    @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String descricao;

    @NotNull(message = "A data de início é obrigatória")
    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @NotNull(message = "A data de término é obrigatória")
    @Column(name = "data_fim")
    private LocalDate dataFim;

    @NotBlank(message = "O local do evento é obrigatório")
    @Size(max = 200, message = "O local deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private StatusEvento status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime agora = OffsetDateTime.now();

        createdAt = agora;
        updatedAt = agora;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public void editarEvento(EventoRequestDTO dto) {
        this.nome = dto.nome();
        this.edicao = dto.edicao();
        this.descricao = dto.descricao();
        this.local = dto.local();
        this.dataInicio = dto.dataInicio();
        this.dataFim = dto.dataFim();
        this.status = dto.status();
    }
}