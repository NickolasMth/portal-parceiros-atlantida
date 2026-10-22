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
@Table(name = "cota", uniqueConstraints = {@UniqueConstraint(name = "uq_cota_1", columnNames = {"plano_comercial_id", "codigo"})})
@Getter
@Setter
public class Cota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 45)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plano_comercial_id", nullable = false)
    private PlanoComercial planoComercial;

    @Column(name = "descricao", length = 200)
    private String descricao;

    @Column(name = "valor", precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(name = "limite_patrocinio")
    private Integer limitePatrocinio;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "beneficios", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> beneficios = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
