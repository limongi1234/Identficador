package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusEntregaDTO(

    @NotNull(message = "Status da entrega é obrigatório")
    StatusEntrega novoStatus,

    String motivoCancelamento,

    String observacoes

) {}
