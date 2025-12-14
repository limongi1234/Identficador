package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para realizar login no sistema")
public class LoginDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(
            description = "Email de login do usuário",
            example = "entregador@email.com",
            required = true
    )
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(
            description = "Senha de acesso",
            example = "senha123",
            required = true,
            minLength = 6
    )
    private String senha;
}