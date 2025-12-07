package br.com.identificador.Back_end.repository;

import br.com.identificador.Back_end.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca usuário por telefone
     */
    Optional<User> findByTelefone(String telefone);
    
    /**
     * Verifica se existe usuário com o telefone
     */
    boolean existsByTelefone(String telefone);
    
    /**
     * Busca usuário por email ignorando case
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);
    
    /**
     * Busca usuários por nome contendo string (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<User> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    /**
     * Busca usuários criados após uma data específica
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    /**
     * Busca usuários atualizados após uma data específica
     */
    @Query("SELECT u FROM User u WHERE u.updatedAt > :date ORDER BY u.updatedAt DESC")
    List<User> findUsersUpdatedAfter(@Param("date") LocalDateTime date);
    
    /**
     * Conta usuários criados após uma data
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :date")
    long countUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    /**
     * Busca todos os emails cadastrados
     */
    @Query("SELECT u.email FROM User u ORDER BY u.email")
    List<String> findAllEmails();
    
    /**
     * Busca usuários ativos (pode ser expandido com campo 'ativo' no futuro)
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findAllOrderByCreatedAtDesc();
}