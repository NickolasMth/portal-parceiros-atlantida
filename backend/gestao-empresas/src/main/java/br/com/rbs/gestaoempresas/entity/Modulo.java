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
@Table(name = "modulo")
@Getter
@Setter
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "descricao", length = 200)
    private String descricao;

    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
