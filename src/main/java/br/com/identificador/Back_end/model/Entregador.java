package br.com.identificador.Back_end.model;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidade que representa um entregador no sistema. Herda de User e adiciona informações específicas como documentos, QR Code e status de disponibilidade.")
public class Entregador extends User {

    @NotBlank
    @Size(max = 20)
    @Schema(description = "CPF do entregador (apenas números)", example = "12345678900", required = true, maxLength = 20)
    private String cpf;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "RG do entregador", example = "123456789", required = true, maxLength = 20)
    private String rg;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "CNH (Carteira Nacional de Habilitação) do entregador", example = "12345678900", required = true, maxLength = 20)
    private String cnh;

    @Column(name = "qr_code_uuid")
    @Schema(description = "UUID único do QR Code gerado para identificação do entregador", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private String qrCodeUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Schema(description = "Status atual de disponibilidade do entregador", example = "DISPONIVEL", defaultValue = "OFFLINE")
    private StatusEntregador status = StatusEntregador.OFFLINE;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "entregador_aplicativos",
            joinColumns = @JoinColumn(name = "entregador_id"))
    @Column(name = "aplicativo")
    @Schema(description = "Lista de aplicativos de delivery que o entregador utiliza", example = "[\"IFOOD\", \"RAPPI\", \"UBER_EATS\"]")
    private Set<Aplicativo> aplicativos = new HashSet<>();

    @Column(name = "avaliacao_media")
    @Schema(description = "Avaliação média do entregador (0.0 a 5.0)", example = "4.5", minimum = "0", maximum = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Double avaliacaoMedia = 0.0;

    @Column(name = "total_entregas")
    @Schema(description = "Número total de entregas realizadas pelo entregador", example = "150", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalEntregas = 0;

    public Entregador(String nome, String email, String telefone, String senha,
                      String cpf, String rg, String cnh) {
        super(nome, email, telefone, senha);
        this.cpf = cpf;
        this.rg = rg;
        this.cnh = cnh;
    }
}