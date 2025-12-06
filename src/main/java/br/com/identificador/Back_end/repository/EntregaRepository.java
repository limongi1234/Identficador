package br.com.identificador.Back_end.repository;

import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.model.Entrega;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    // Buscar por entregador e status
    List<Entrega> findByEntregadorAndStatusEntrega(Entregador entregador, StatusEntrega statusEntrega);

    // Histórico de entregas do entregador
    List<Entrega> findByEntregadorOrderByCriadoEmDesc(Entregador entregador);

    // Buscar entregas por status
    List<Entrega> findByStatusEntregaOrderByCriadoEmDesc(StatusEntrega statusEntrega);

    // Entregas de uma loja
    List<Entrega> findByLojaOrderByCriadoEmDesc(Loja loja);

    // Entregas de um cliente
    List<Entrega> findByClienteOrderByCriadoEmDesc(Cliente cliente);

    // Entregas pendentes (sem entregador)
    @Query("SELECT e FROM Entrega e WHERE e.statusEntrega = 'PENDENTE' AND e.entregador IS NULL ORDER BY e.criadoEm ASC")
    List<Entrega> buscarEntregasPendentes();

    // Entregas em andamento de um entregador
    @Query("SELECT e FROM Entrega e WHERE e.entregador = :entregador AND e.statusEntrega IN ('ACEITA', 'EM_ANDAMENTO', 'COLETADA', 'A_CAMINHO')")
    List<Entrega> buscarEntregasEmAndamento(@Param("entregador") Entregador entregador);

    // Estatísticas do entregador
    @Query("SELECT COUNT(e) FROM Entrega e WHERE e.entregador = :entregador AND e.statusEntrega = 'ENTREGUE'")
    Long contarEntregasConcluidasPorEntregador(@Param("entregador") Entregador entregador);

    // Entregas por período
    @Query("SELECT e FROM Entrega e WHERE e.criadoEm BETWEEN :dataInicio AND :dataFim ORDER BY e.criadoEm DESC")
    List<Entrega> buscarEntregasPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                           @Param("dataFim") LocalDateTime dataFim);

    // Entregas canceladas
    @Query("SELECT e FROM Entrega e WHERE e.statusEntrega = 'CANCELADA' AND e.canceladoEm BETWEEN :dataInicio AND :dataFim")
    List<Entrega> buscarEntregasCanceladas(@Param("dataInicio") LocalDateTime dataInicio,
                                           @Param("dataFim") LocalDateTime dataFim);
}
