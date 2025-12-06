package br.com.identificador.Back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarEntregaDTO {

    @NotNull(message = "ID da loja é obrigatório")
    private Long lojaId;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "Endereço de origem é obrigatório")
    private String enderecoOrigem;

    @NotBlank(message = "Endereço de destino é obrigatório")
    private String enderecoDestino;

    @NotBlank(message = "Descrição do produto é obrigatória")
    private String descricaoProduto;

    @NotNull(message = "Valor da entrega é obrigatório")
    @Positive(message = "Valor da entrega deve ser positivo")
    private BigDecimal valorEntrega;

    private BigDecimal valorGorjeta;

    @Positive(message = "Tempo estimado deve ser positivo")
    private Integer tempoEstimadoMinutos;

    private String observacoes;
}
