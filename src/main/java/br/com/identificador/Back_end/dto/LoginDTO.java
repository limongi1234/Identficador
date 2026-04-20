package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para realizar login no sistema")
public record LoginDTO(

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(
            description = "Email de login do usuário",
            example = "entregador@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Schema(
            description = "Senha de acesso",
            example = "senha123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 6
    )
    String senha

) {}
