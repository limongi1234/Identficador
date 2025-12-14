package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusEntregaDTO {

    @NotNull(message = "Status da entrega é obrigatório")
    private StatusEntrega novoStatus;

    private String motivoCancelamento;

    private String observacoes;
}
