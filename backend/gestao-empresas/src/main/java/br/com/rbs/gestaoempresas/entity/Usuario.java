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
@Table(name = "usuario")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrocinador_id")
    private Patrocinador patrocinador;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 200)
    private String senhaHash;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ATIVO";

    @Column(name = "ultimo_acesso")
    private Instant ultimoAcesso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
