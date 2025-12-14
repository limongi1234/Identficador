package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para registro ou atualização de entregador")
public class EntregadorRegistroDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(
            description = "Nome completo do entregador",
            example = "João da Silva Santos",
            required = true,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email do entregador (será usado para login)",
            example = "joao.silva@email.com",
            required = true,
            maxLength = 100
    )
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato com DDD",
            example = "(21) 98765-4321",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Schema(
            description = "Senha de acesso",
            example = "senhaSegura123",
            required = true,
            minLength = 6,
            maxLength = 100
    )
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 20)
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Schema(
            description = "CPF do entregador (apenas números)",
            example = "12345678900",
            required = true,
            pattern = "\\d{11}",
            maxLength = 20
    )
    private String cpf;

    @NotBlank(message = "RG é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "RG do entregador",
            example = "123456789",
            required = true,
            maxLength = 20
    )
    private String rg;

    @NotBlank(message = "CNH é obrigatória")
    @Size(max = 20)
    @Schema(
            description = "CNH (Carteira Nacional de Habilitação)",
            example = "12345678900",
            required = true,
            maxLength = 20
    )
    private String cnh;

    @Schema(
            description = "Lista de aplicativos que o entregador trabalha",
            example = "[\"IFOOD\", \"RAPPI\", \"UBER_EATS\"]"
    )
    private Set<Aplicativo> aplicativos = new HashSet<>();
}

