package br.com.identificador.Back_end.repository;

import br.com.identificador.Back_end.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
    Optional<Loja> findByEmail(String email);
    Optional<Loja> findByCnpj(String cnpj);

    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);

    @Query("SELECT l FROM Loja l WHERE LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Loja> buscarPorNome(@Param("nome") String nome);

    @Query("SELECT l FROM Loja l WHERE LOWER(l.endereco) LIKE LOWER(CONCAT('%', :endereco, '%'))")
    List<Loja> buscarPorEndereco(@Param("endereco") String endereco);
}
