package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados genéricos para registro/atualização de usuário")
public record UserRegistrationDTO(

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do usuário",
            example = "Pedro Alves",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email do usuário",
            example = "pedro.alves@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    String email,

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 99999-8888",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 20
    )
    String telefone,

    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso (opcional em atualizações)",
            example = "novaSenha123",
            minLength = 6,
            maxLength = 100
    )
    String senha

) {}
