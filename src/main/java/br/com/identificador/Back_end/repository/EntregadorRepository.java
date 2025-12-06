package br.com.identificador.Back_end.repository;

import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {
    Optional<Entregador> findByEmail(String email);
    Optional<Entregador> findByQrCodeUuid(String qrCodeUuid);
    Optional<Entregador> findByCpf(String cpf);

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    List<Entregador> findByStatus(StatusEntregador status);

    @Query("SELECT e FROM Entregador e WHERE e.status = :status AND SIZE(e.aplicativos) > 0")
    List<Entregador> buscarEntregadoresDisponiveis(@Param("status") StatusEntregador status);

    @Query("SELECT e FROM Entregador e WHERE e.avaliacaoMedia >= :avaliacaoMinima ORDER BY e.avaliacaoMedia DESC")
    List<Entregador> buscarPorAvaliacaoMinima(@Param("avaliacaoMinima") Double avaliacaoMinima);
}
