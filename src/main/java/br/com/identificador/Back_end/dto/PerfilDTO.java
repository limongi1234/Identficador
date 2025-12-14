package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do perfil do entregador")
public class PerfilDTO {

    @Schema(
            description = "ID único do perfil",
            example = "1"
    )
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do entregador",
            example = "Pedro Alves",
            required = true,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 99999-8888",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @Schema(
            description = "Status atual do entregador",
            example = "ATIVO"
    )
    private StatusEntregador status;

    @Schema(
            description = "Aplicativos associados ao entregador"
    )
    private Set<Aplicativo> aplicativos;

    @Schema(
            description = "Avaliação média do entregador",
            example = "4.8"
    )
    private Double avaliacaoMedia;

    @Schema(
            description = "Total de entregas realizadas",
            example = "150"
    )
    private Integer totalEntregas;

    @Schema(
            description = "UUID do QR Code do perfil",
            example = "abc123-def456-ghi789"
    )
    private String qrCodeUuid;
}
