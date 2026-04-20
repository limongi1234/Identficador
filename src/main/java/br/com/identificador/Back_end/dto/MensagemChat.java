package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Mensagem do chat em tempo real via WebSocket")
public record MensagemChat(

    @Schema(
            description = "Nome do remetente da mensagem",
            example = "João Silva"
    )
    String remetente,

    @Schema(
            description = "Conteúdo da mensagem",
            example = "Olá, estou a caminho!"
    )
    String conteudo,

    @Schema(
            description = "Tipo da mensagem (CHAT, JOIN, LEAVE)",
            example = "CHAT"
    )
    TipoMensagem tipo,

    @Schema(
            description = "ID do destinatário (para mensagens privadas)",
            example = "5"
    )
    Long destinatarioId,

    @Schema(
            description = "Data e hora de envio",
            example = "2024-01-15T14:30:00"
    )
    LocalDateTime timestamp

) {

    @Schema(description = "Tipos de mensagem disponíveis no chat")
    public enum TipoMensagem {
        @Schema(description = "Mensagem de chat normal")
        CHAT,

        @Schema(description = "Usuário entrou no chat")
        JOIN,

        @Schema(description = "Usuário saiu do chat")
        LEAVE
    }
}