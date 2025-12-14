package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados completos de uma entrega (usado em respostas)")
public class EntregaDTO {

    @Schema(description = "ID único da entrega", example = "1")
    private Long id;

    @Schema(description = "ID do entregador responsável", example = "3")
    private Long entregadorId;

    @Schema(description = "Nome do entregador", example = "João Silva")
    private String entregadorNome;

    @Schema(description = "ID da loja origem", example = "1")
    private Long lojaId;

    @Schema(description = "Nome da loja", example = "Pizzaria Bella Napoli")
    private String lojaNome;

    @Schema(description = "ID do cliente destinatário", example = "5")
    private Long clienteId;

    @Schema(description = "Nome do cliente", example = "Maria Santos")
    private String clienteNome;

    @Schema(description = "Endereço de coleta", example = "Av. Principal, 1000 - Centro")
    private String enderecoOrigem;

    @Schema(description = "Endereço de entrega", example = "Rua das Flores, 123 - Apt 201")
    private String enderecoDestino;

    @Schema(description = "Descrição dos produtos", example = "1 Pizza Grande + 1 Refrigerante")
    private String produtoDescricao;

    @Schema(description = "Status atual da entrega", example = "A_CAMINHO_ENTREGA")
    private StatusEntrega statusEntrega;

    @Schema(description = "Valor da entrega", example = "15.50")
    private BigDecimal valorEntrega;

    @Schema(description = "Valor da gorjeta", example = "5.00")
    private BigDecimal valorGorjeta;

    @Schema(description = "Tempo estimado em minutos", example = "30")
    private Integer tempoEstimadoMinutos;

    @Schema(description = "Observações adicionais")
    private String observacoes;

    @Schema(description = "Data/hora de criação", example = "2024-01-15T14:30:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data/hora de início", example = "2024-01-15T14:45:00")
    private LocalDateTime iniciadoEm;

    @Schema(description = "Data/hora de finalização", example = "2024-01-15T15:15:00")
    private LocalDateTime finalizadoEm;

    @Schema(description = "Data/hora de cancelamento")
    private LocalDateTime canceladoEm;
}