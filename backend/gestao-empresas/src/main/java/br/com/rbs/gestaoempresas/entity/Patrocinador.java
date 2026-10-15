package br.com.rbs.gestaoempresas.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "patrocinador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome fantasia é obrigatório")
    @Size(max = 45, message = "O nome fantasia deve ter no máximo 45 caracteres")
    @Column(name = "nome_fantasia", length = 45)
    private String nomeFantasia;

    @NotBlank(message = "A razão social é obrigatória")
    @Size(max = 200, message = "A razão social deve ter no máximo 200 caracteres")
    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @NotBlank(message = "O CNPJ é obrigatório")
    @Size(max = 20, message = "O CNPJ deve ter no máximo 20 caracteres")
    @Pattern(
            regexp = "\\d{14}",
            message = "O CNPJ deve conter exatamente 14 dígitos numéricos"
    )
    @Column(length = 20)
    private String cnpj;

    @NotBlank(message = "O e-mail de contato é obrigatório")
    @Email(message = "O e-mail informado é inválido")
    @Size(max = 200, message = "O e-mail deve ter no máximo 200 caracteres")
    @Column(name = "email_contato", length = 200)
    private String emailContato;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String telefone;

    @Size(max = 200, message = "O website deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String website;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "endereco", columnDefinition = "jsonb")
    private JsonNode endereco;

    @NotNull(message = "O status do patrocinador é obrigatório")
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime agora = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = agora;
        }

        if (updatedAt == null) {
            updatedAt = agora;
        }

        if (ativo == null) {
            ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}