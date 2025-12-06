package br.com.identificador.Back_end.model;

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
public class Loja extends User {

    @NotBlank
    @Size(max = 20)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    private String endereco;

    @Size(max = 100)
    private String responsavel;

    @Size(max = 100)
    private String horarioFuncionamento;

    public Loja(String nome, String email, String telefone, String senha,
                String cnpj, String endereco) {
        super(nome, email, telefone, senha);
        this.cnpj = cnpj;
        this.endereco = endereco;
    }
}
