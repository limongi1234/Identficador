package br.com.identificador.Back_end.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lojas")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Entidade que representa uma loja/estabelecimento comercial que utiliza o sistema de entregas")
public class Loja extends User {

    @NotBlank
    @Size(max = 20)
    @Schema(description = "CNPJ da loja (apenas números)", example = "12345678000190", required = true, maxLength = 20)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    @Schema(description = "Endereço completo da loja", example = "Av. Principal, 1000 - Sala 201 - Centro - Rio de Janeiro/RJ", required = true, maxLength = 200)
    private String endereco;

    @Size(max = 100)
    @Schema(description = "Nome do responsável pela loja", example = "Maria Santos", maxLength = 100)
    private String responsavel;

    @Size(max = 100)
    @Schema(description = "Horário de funcionamento da loja", example = "Seg-Sex: 9h às 18h / Sáb: 9h às 13h", maxLength = 100)
    private String horarioFuncionamento;

    public Loja(String nome, String email, String telefone, String senha,
                String cnpj, String endereco) {
        super(nome, email, telefone, senha);
        this.cnpj = cnpj;
        this.endereco = endereco;
    }
}