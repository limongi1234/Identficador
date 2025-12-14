package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para registro ou atualização de loja")
public class LojaRegistroDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome da loja/estabelecimento",
            example = "Pizzaria Bella Napoli",
            required = true,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100)
    @Schema(
            description = "Email da loja (usado para login)",
            example = "contato@bellanapoli.com",
            required = true,
            maxLength = 100
    )
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone comercial da loja",
            example = "(21) 3333-4444",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso",
            example = "senhaLoja123",
            required = true,
            minLength = 6,
            maxLength = 100
    )
    private String senha;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 20)
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @Schema(
            description = "CNPJ da loja (apenas números)",
            example = "12345678000190",
            required = true,
            pattern = "\\d{14}",
            maxLength = 20
    )
    private String cnpj;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200)
    @Schema(
            description = "Endereço completo da loja",
            example = "Av. Principal, 1000 - Sala 201 - Centro - Rio de Janeiro/RJ - CEP: 20000-000",
            required = true,
            maxLength = 200
    )
    private String endereco;

    @Size(max = 100)
    @Schema(
            description = "Nome do responsável pela loja",
            example = "Carlos Oliveira",
            maxLength = 100
    )
    private String responsavel;

    @Size(max = 100)
    @Schema(
            description = "Horário de funcionamento",
            example = "Segunda a Sexta: 11h às 23h / Sábado e Domingo: 11h às 00h",
            maxLength = 100
    )
    private String horarioFuncionamento;
}