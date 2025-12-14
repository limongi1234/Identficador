package br.com.identificador.Back_end.model;

import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
@Schema(description = "Entidade que representa uma entrega no sistema, relacionando loja, entregador e cliente")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único da entrega", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    @Schema(description = "Entregador responsável pela entrega")
    private Entregador entregador;

    @ManyToOne
    @JoinColumn(name = "loja_id")
    @Schema(description = "Loja origem da entrega", required = true)
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @Schema(description = "Cliente destinatário da entrega")
    private Cliente cliente;

    @Column(name = "endereco_origem")
    @Schema(description = "Endereço de coleta do produto", example = "Av. Principal, 1000 - Centro")
    private String enderecoOrigem;

    @Column(name = "endereco_destino", nullable = false)
    @Schema(description = "Endereço de entrega do produto", example = "Rua das Flores, 123 - Bairro", required = true)
    private String enderecoDestino;

    @Column(name = "produto_descricao")
    @Schema(description = "Descrição do(s) produto(s) a ser(em) entregue(s)", example = "1 Pizza Grande + 1 Refrigerante 2L")
    private String produtoDescricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_entrega", nullable = false)
    @Schema(description = "Status atual da entrega", example = "A_CAMINHO_COLETA",
            allowableValues = {"A_CAMINHO_COLETA", "COLETANDO", "A_CAMINHO_ENTREGA", "CHEGOU_DESTINO", "ENTREGUE", "CANCELADA", "PROBLEMA"})
    private StatusEntrega statusEntrega;

    @Column(name = "valor_entrega", precision = 10, scale = 2)
    @Schema(description = "Valor do frete da entrega", example = "15.50")
    private BigDecimal valorEntrega;

    @Column(name = "valor_gorjeta", precision = 10, scale = 2)
    @Schema(description = "Valor da gorjeta para o entregador", example = "5.00")
    private BigDecimal valorGorjeta;

    @Column(name = "tempo_estimado_minutos")
    @Schema(description = "Tempo estimado de entrega em minutos", example = "30")
    private Integer tempoEstimadoMinutos;

    @Column(name = "observacoes", length = 500)
    @Schema(description = "Observações adicionais sobre a entrega", example = "Tocar interfone apartamento 201")
    private String observacoes;

    @Column(name = "created_at", updatable = false)
    @Schema(description = "Data e hora de criação da entrega", example = "2024-01-15T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "started_at")
    @Schema(description = "Data e hora de início da entrega", example = "2024-01-15T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime iniciadoEm;

    @Column(name = "finished_at")
    @Schema(description = "Data e hora de finalização da entrega", example = "2024-01-15T15:15:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime finalizadoEm;

    @Column(name = "cancelled_at")
    @Schema(description = "Data e hora de cancelamento da entrega (se aplicável)", example = "2024-01-15T14:50:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime canceladoEm;

    // Construtor auxiliar para criação
    public Entrega(Loja loja, Cliente cliente, String enderecoOrigem, String enderecoDestino,
                   String produtoDescricao, BigDecimal valorEntrega, Integer tempoEstimadoMinutos) {
        this.loja = loja;
        this.cliente = cliente;
        this.enderecoOrigem = enderecoOrigem;
        this.enderecoDestino = enderecoDestino;
        this.produtoDescricao = produtoDescricao;
        this.valorEntrega = valorEntrega;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.statusEntrega = StatusEntrega.A_CAMINHO_COLETA; // Status inicial correto
        this.criadoEm = LocalDateTime.now();
    }
}
