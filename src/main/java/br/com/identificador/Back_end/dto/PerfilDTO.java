package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

@Schema(description = "Dados do perfil do entregador")
public record PerfilDTO(

    @Schema(
            description = "ID único do perfil",
            example = "1"
    )
    Long id,

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do entregador",
            example = "Pedro Alves",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    String nome,

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(
            description = "Telefone de contato",
            example = "(21) 99999-8888",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 20
    )
    String telefone,

    @Schema(
            description = "Status atual do entregador",
            example = "ATIVO"
    )
    StatusEntregador status,

    @Schema(
            description = "Aplicativos associados ao entregador"
    )
    Set<Aplicativo> aplicativos,

    @Schema(
            description = "Avaliação média do entregador",
            example = "4.8"
    )
    Double avaliacaoMedia,

    @Schema(
            description = "Total de entregas realizadas",
            example = "150"
    )
    Integer totalEntregas,

    @Schema(
            description = "UUID do QR Code do perfil",
            example = "abc123-def456-ghi789"
    )
    String qrCodeUuid

) {}
