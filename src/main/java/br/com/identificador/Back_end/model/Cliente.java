package br.com.identificador.Back_end.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Cliente extends User {

    @Size(max = 20)
    private String cpf;

    @Size(max = 200)
    private String endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Entrega> entregas = new ArrayList<>();

    public Cliente(String nome, String email, String telefone, String senha) {
        super(nome, email, telefone, senha);
    }
}
