package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para registro ou atualização de cliente")
public class ClienteRegistroDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do cliente",
            example = "Maria Santos",
            required = true,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email do cliente (usado para login)",
            example = "maria.santos@email.com",
            required = true,
            maxLength = 100
    )
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 98888-7777",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso",
            example = "senha123",
            required = true,
            minLength = 6,
            maxLength = 100
    )
    private String senha;

    @Size(max = 20)
    @Schema(
            description = "CPF do cliente (opcional)",
            example = "12345678900",
            maxLength = 20
    )
    private String cpf;

    @Size(max = 200)
    @Schema(
            description = "Endereço principal do cliente",
            example = "Rua das Flores, 123 - Apt 201 - Centro - Rio de Janeiro/RJ - CEP: 20000-000",
            maxLength = 200
    )
    private String endereco;
}