package br.com.identificador.Back_end.model.enuns;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status da entrega",
        allowableValues = {"A_CAMINHO_COLETA", "COLETANDO", "A_CAMINHO_ENTREGA",
                "CHEGOU_DESTINO", "ENTREGUE", "CANCELADA", "PROBLEMA"})
public enum StatusEntrega {
    @Schema(description = "Entregador está a caminho do local de coleta")
    A_CAMINHO_COLETA("Entregador está a caminho do local de coleta"),

    @Schema(description = "Entregador chegou no local de coleta")
    COLETANDO("Entregador chegou no local de coleta"),

    @Schema(description = "Produto coletado, entregador a caminho do destino")
    A_CAMINHO_ENTREGA("Produto coletado, entregador a caminho do destino"),

    @Schema(description = "Entregador chegou no endereço de entrega")
    CHEGOU_DESTINO("Entregador chegou no endereço de entrega"),

    @Schema(description = "Entrega realizada com sucesso")
    ENTREGUE("Entrega realizada com sucesso"),

    @Schema(description = "Entrega foi cancelada")
    CANCELADA("Entrega foi cancelada"),

    @Schema(description = "Problema na entrega (destinatário ausente, endereço incorreto, etc)")
    PROBLEMA("Problema na entrega");

    private final String descricao;

    StatusEntrega(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
