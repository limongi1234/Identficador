package br.com.identificador.Back_end.model.enuns;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status de disponibilidade do entregador no sistema",
        allowableValues = {"OFFLINE", "DISPONIVEL", "EM_ROTA", "OCUPADO", "PAUSADO", "INATIVO"})
public enum StatusEntregador {

    @Schema(description = "Entregador está offline/desconectado")
    OFFLINE("Offline"),

    @Schema(description = "Entregador está online e disponível para aceitar entregas")
    DISPONIVEL("Disponível"),

    @Schema(description = "Entregador está em rota realizando uma entrega")
    EM_ROTA("Em Rota"),

    @Schema(description = "Entregador está ocupado mas ainda online")
    OCUPADO("Ocupado"),

    @Schema(description = "Entregador pausou temporariamente para descanso/refeição")
    PAUSADO("Pausado"),

    @Schema(description = "Entregador teve cadastro inativado pelo sistema")
    INATIVO("Inativo");

    private final String descricao;

    StatusEntregador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
