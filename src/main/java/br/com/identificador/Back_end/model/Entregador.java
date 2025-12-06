package br.com.identificador.Back_end.model;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "entregadores")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Entregador extends User {

    @NotBlank
    @Size(max = 20)
    private String cpf;

    @NotBlank
    @Size(max = 20)
    private String rg;

    @NotBlank
    @Size(max = 20)
    private String cnh;

    @Column(name = "qr_code_uuid")
    private String qrCodeUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEntregador status = StatusEntregador.OFFLINE;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "entregador_aplicativos",
            joinColumns = @JoinColumn(name = "entregador_id"))
    @Column(name = "aplicativo")
    private Set<Aplicativo> aplicativos = new HashSet<>();

    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia = 0.0;

    @Column(name = "total_entregas")
    private Integer totalEntregas = 0;

    public Entregador(String nome, String email, String telefone, String senha,
                      String cpf, String rg, String cnh) {
        super(nome, email, telefone, senha);
        this.cpf = cpf;
        this.rg = rg;
        this.cnh = cnh;
    }
}
