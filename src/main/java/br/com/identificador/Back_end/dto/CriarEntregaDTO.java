package br.com.identificador.Back_end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de uma nova entrega")
public class CriarEntregaDTO {

    @NotNull(message = "ID da loja é obrigatório")
    @Schema(
            description = "ID da loja que está solicitando a entrega",
            example = "1",
            required = true
    )
    private Long lojaId;

    @Schema(
            description = "ID do cliente destinatário (opcional)",
            example = "5"
    )
    private Long clienteId;

    @NotBlank(message = "Endereço de origem é obrigatório")
    @Schema(
            description = "Endereço de coleta do produto",
            example = "Av. Principal, 1000 - Centro - Rio de Janeiro/RJ",
            required = true
    )
    private String enderecoOrigem;

    @NotBlank(message = "Endereço de destino é obrigatório")
    @Schema(
            description = "Endereço de entrega do produto",
            example = "Rua das Flores, 123 - Apt 201 - Bairro - Rio de Janeiro/RJ",
            required = true
    )
    private String enderecoDestino;

    @Schema(
            description = "Descrição do(s) produto(s) a ser(em) entregue(s)",
            example = "1 Pizza Grande Calabresa + 1 Refrigerante 2L + 1 Sorvete"
    )
    private String produtoDescricao;

    @NotNull(message = "Valor da entrega é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    @Schema(
            description = "Valor do frete/entrega",
            example = "15.50",
            required = true,
            minimum = "0.01"
    )
    private BigDecimal valorEntrega;

    @DecimalMin(value = "0.0", message = "Gorjeta não pode ser negativa")
    @Schema(
            description = "Valor da gorjeta para o entregador (opcional)",
            example = "5.00",
            minimum = "0"
    )
    private BigDecimal valorGorjeta;

    @Min(value = 1, message = "Tempo estimado deve ser no mínimo 1 minuto")
    @Schema(
            description = "Tempo estimado de entrega em minutos",
            example = "30",
            minimum = "1"
    )
    private Integer tempoEstimadoMinutos;

    @Schema(
            description = "Observações adicionais sobre a entrega",
            example = "Tocar interfone do apartamento 201. Cliente pediu sem cebola."
    )
    private String observacoes;
}
