package br.com.rbs.gestaoempresas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "patrocinio", uniqueConstraints = {@UniqueConstraint(name = "uq_patrocinio_1", columnNames = {"evento_id", "patrocinador_id"})})
@Getter
@Setter
public class Patrocinio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cota_id", nullable = false)
    private Cota cota;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ATIVO";

    @Column(name = "valor_acordado", precision = 14, scale = 2)
    private BigDecimal valorAcordado;

    @Column(name = "data_contratacao")
    private LocalDate dataContratacao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "observacao", length = 200)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patrocinador_id", nullable = false)
    private Patrocinador patrocinador;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
