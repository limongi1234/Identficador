package br.com.identificador.Back_end.model;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @ManyToOne
    @JoinColumn(name = "loja_id")
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "endereco_origem")
    private String enderecoOrigem;

    @Column(name = "endereco_destino")
    private String enderecoDestino;

    @Column(name = "produto_descricao")
    private String produtoDescricao;

    @Enumerated(EnumType.STRING)
    private StatusEntrega statusEntrega = StatusEntrega.PENDENTE;

    @Column(name = "valor_entrega")
    private BigDecimal valorEntrega;

    @Column(name = "valor_gorjeta")
    private BigDecimal valorGorjeta;

    @Column(name = "tempo_estimado_minutos")
    private Integer tempoEstimadoMinutos;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "created_at")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "started_at")
    private LocalDateTime iniciadoEm;

    @Column(name = "finished_at")
    private LocalDateTime finalizadoEm;

    @Column(name = "cancelled_at")
    private LocalDateTime canceladoEm;
}
