package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mensagem do chat em tempo real via WebSocket")
public class MensagemChat {

    @Schema(
            description = "Nome do remetente da mensagem",
            example = "João Silva"
    )
    private String remetente;

    @Schema(
            description = "Conteúdo da mensagem",
            example = "Olá, estou a caminho!"
    )
    private String conteudo;

    @Schema(
            description = "Tipo da mensagem (CHAT, JOIN, LEAVE)",
            example = "CHAT"
    )
    private TipoMensagem tipo;

    @Schema(
            description = "ID do destinatário (para mensagens privadas)",
            example = "5"
    )
    private Long destinatarioId;

    @Schema(
            description = "Data e hora de envio",
            example = "2024-01-15T14:30:00"
    )
    private LocalDateTime timestamp = LocalDateTime.now();

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