package br.com.identificador.Back_end.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classe base abstrata para todos os tipos de usuários do sistema")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome completo do usuário", example = "João da Silva", required = true, maxLength = 100)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(description = "Email do usuário (usado para login)", example = "joao.silva@email.com", required = true, maxLength = 100)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Telefone de contato", example = "(21) 98765-4321", required = true, maxLength = 20)
    private String telefone;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Senha de acesso", example = "senha123", required = true, minLength = 6, maxLength = 100, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String senha;

    @Column(name = "created_at")
    @Schema(description = "Data e hora de criação do registro", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Schema(description = "Data e hora da última atualização", example = "2024-01-20T15:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public User(String nome, String email, String telefone, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}