package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados para registro ou atualização de cliente")
public record ClienteRegistroDTO(

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do cliente",
            example = "Maria Santos",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email do cliente (usado para login)",
            example = "maria.santos@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    String email,

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 98888-7777",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 20
    )
    String telefone,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso",
            example = "senha123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 6,
            maxLength = 100
    )
    String senha,

    @Size(max = 20)
    @Schema(
            description = "CPF do cliente (opcional)",
            example = "12345678900",
            maxLength = 20
    )
    String cpf,

    @Size(max = 200)
    @Schema(
            description = "Endereço principal do cliente",
            example = "Rua das Flores, 123 - Apt 201 - Centro - Rio de Janeiro/RJ - CEP: 20000-000",
            maxLength = 200
    )
    String endereco

) {}
