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
@Table(name = "email_liberado")
@Getter
@Setter
public class EmailLiberado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastrado_por_usuario_id")
    private Usuario cadastradoPorUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aceito_por_usuario_id")
    private Usuario aceitoPorUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrocinador_id")
    private Patrocinador patrocinador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "status_convite", nullable = false, length = 30)
    private String statusConvite = "PENDENTE";

    @Column(name = "token_hash", nullable = false, unique = true, length = 200)
    private String tokenHash;

    @Column(name = "valido_ate")
    private Instant validoAte;

    @Column(name = "aceito_em")
    private Instant aceitoEm;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
