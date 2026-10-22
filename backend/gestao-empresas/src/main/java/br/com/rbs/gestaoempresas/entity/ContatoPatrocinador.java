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
@Table(name = "contato_patrocinador")
@Getter
@Setter
public class ContatoPatrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patrocinador_id", nullable = false)
    private Patrocinador patrocinador;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "email_contato", length = 200)
    private String emailContato;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "cargo", length = 200)
    private String cargo;

    @Column(name = "tipo_contato", length = 30)
    private String tipoContato;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "principal", nullable = false)
    private boolean principal = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
