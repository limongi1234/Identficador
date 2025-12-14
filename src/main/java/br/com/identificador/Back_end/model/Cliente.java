package br.com.identificador.Back_end.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidade que representa um cliente do sistema. Clientes podem solicitar entregas.")
public class Cliente extends User {

    @Size(max = 20)
    @Schema(description = "CPF do cliente (opcional)", example = "12345678900", maxLength = 20)
    private String cpf;

    @Size(max = 200)
    @Schema(description = "Endereço principal do cliente", example = "Rua das Flores, 123 - Centro - Rio de Janeiro/RJ", maxLength = 200)
    private String endereco;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Schema(description = "Lista de entregas solicitadas pelo cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Entrega> entregas = new ArrayList<>();

    public Cliente(String nome, String email, String telefone, String senha) {
        super(nome, email, telefone, senha);
    }
}