package br.com.identificador.Back_end.model.enuns;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de mensagens no sistema de chat",
        allowableValues = {"CHAT", "ENTRADA", "SAIDA", "SISTEMA"})
public enum TipoMensagem {

    @Schema(description = "Mensagem de chat normal entre usuários")
    CHAT("Chat"),

    @Schema(description = "Mensagem de entrada no chat")
    ENTRADA("Entrada"),

    @Schema(description = "Mensagem de saída do chat")
    SAIDA("Saída"),

    @Schema(description = "Mensagem do sistema/notificação")
    SISTEMA("Sistema");

    private final String descricao;

    TipoMensagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
