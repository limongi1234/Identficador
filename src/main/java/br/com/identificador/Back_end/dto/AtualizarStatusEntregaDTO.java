package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização do status de uma entrega")
public class AtualizarStatusEntregaDTO {

    @NotNull(message = "Novo status é obrigatório")
    @Schema(
            description = "Novo status da entrega",
            example = "A_CAMINHO_ENTREGA",
            required = true
    )
    private StatusEntrega novoStatus;

    @Schema(
            description = "Observações sobre a mudança de status",
            example = "Produto coletado com sucesso. Indo para o endereço de entrega."
    )
    private String observacao;
}