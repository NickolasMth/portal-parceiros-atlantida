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
@Table(name = "cota_modulo", uniqueConstraints = {@UniqueConstraint(name = "uq_cota_modulo_1", columnNames = {"modulo_id", "cota_id"})})
@Getter
@Setter
public class CotaModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cota_id", nullable = false)
    private Cota cota;

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;
}
