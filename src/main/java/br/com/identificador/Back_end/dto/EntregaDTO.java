package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDTO {
    private Long id;
    private Long entregadorId;
    private String nomeEntregador;
    private Long lojaId;
    private String nomeLoja;
    private Long clienteId;
    private String nomeCliente;
    private String enderecoOrigem;
    private String enderecoDestino;
    private String descricaoProduto;
    private StatusEntrega statusEntrega;
    private BigDecimal valorEntrega;
    private BigDecimal valorGorjeta;
    private BigDecimal valorTotal;
    private Integer tempoEstimadoMinutos;
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDateTime canceladoEm;

    public void setLojaNome(@NotBlank @Size(max = 100) String nome) {
        this.nomeLoja = nome;
    }

    public void setClienteNome(@NotBlank @Size(max = 100) String nome) {
        this.nomeCliente = nome;
    }

    public void setEntregadorNome(@NotBlank @Size(max = 100) String nome) {
        this.nomeEntregador = nome;
    }
}
