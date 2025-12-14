package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados genéricos para registro/atualização de usuário")
public class UserRegistrationDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do usuário",
            example = "Pedro Alves",
            required = true,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email do usuário",
            example = "pedro.alves@email.com",
            required = true,
            maxLength = 100
    )
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 99999-8888",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso (opcional em atualizações)",
            example = "novaSenha123",
            minLength = 6,
            maxLength = 100
    )
    private String senha;
}