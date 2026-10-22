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
@Table(name = "patrocinador")
@Getter
@Setter
public class Patrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 120)
    private String nomeFantasia;

    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Column(name = "cnpj", unique = true, length = 20)
    private String cnpj;

    @Column(name = "email_contato", length = 200)
    private String emailContato;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "website", length = 200)
    private String website;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "endereco", columnDefinition = "jsonb")
    private Map<String, Object> endereco;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
