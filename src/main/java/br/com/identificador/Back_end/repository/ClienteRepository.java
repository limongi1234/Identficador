package br.com.identificador.Back_end.repository;

import br.com.identificador.Back_end.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByCpf(String cpf);

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> buscarPorNome(@Param("nome") String nome);

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.entregas) >= :minimoEntregas")
    List<Cliente> buscarClientesAtivos(@Param("minimoEntregas") int minimoEntregas);
}
