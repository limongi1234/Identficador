package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EntregaDTO(

    Long id,
    Long entregadorId,
    String nomeEntregador,
    Long lojaId,
    String nomeLoja,
    Long clienteId,
    String nomeCliente,
    String enderecoOrigem,
    String enderecoDestino,
    String descricaoProduto,
    StatusEntrega statusEntrega,
    BigDecimal valorEntrega,
    BigDecimal valorGorjeta,
    BigDecimal valorTotal,
    Integer tempoEstimadoMinutos,
    String observacoes,
    LocalDateTime criadoEm,
    LocalDateTime iniciadoEm,
    LocalDateTime finalizadoEm,
    LocalDateTime canceladoEm

) {}
