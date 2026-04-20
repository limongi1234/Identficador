package br.com.identificador.Back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarEntregaDTO(

    @NotNull(message = "ID da loja é obrigatório")
    Long lojaId,

    @NotNull(message = "ID do cliente é obrigatório")
    Long clienteId,

    @NotBlank(message = "Endereço de origem é obrigatório")
    String enderecoOrigem,

    @NotBlank(message = "Endereço de destino é obrigatório")
    String enderecoDestino,

    @NotBlank(message = "Descrição do produto é obrigatória")
    String descricaoProduto,

    @NotNull(message = "Valor da entrega é obrigatório")
    @Positive(message = "Valor da entrega deve ser positivo")
    BigDecimal valorEntrega,

    BigDecimal valorGorjeta,

    @Positive(message = "Tempo estimado deve ser positivo")
    Integer tempoEstimadoMinutos,

    String observacoes

) {}
