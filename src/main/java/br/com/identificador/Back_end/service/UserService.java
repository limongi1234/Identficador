package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Busca todos os usuários
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("Buscando todos os usuários");
        return userRepository.findAll().stream()
                .peek(user -> log.debug("Usuário encontrado: {}", user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Busca usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    log.debug("Usuário encontrado: {}", user.getEmail());
                    return user;
                });
    }

    /**
     * Busca usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.debug("Usuário encontrado: {}", user.getNome());
                    return user;
                });
    }

    /**
     * Verifica se email já existe
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.info("Verificando se email existe: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.debug("Email {} existe: {}", email, exists);
        return exists;
    }

    /**
     * Busca usuários por nome (busca parcial)
     */
    @Transactional(readOnly = true)
    public List<User> findByNomeContaining(String nome) {
        log.info("Buscando usuários com nome contendo: {}", nome);
        return userRepository.findAll().stream()
                .filter(user -> user.getNome().toLowerCase().contains(nome.toLowerCase()))
                .peek(user -> log.debug("Usuário encontrado: {}", user.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Busca usuários criados após uma data específica
     */
    @Transactional(readOnly = true)
    public List<User> findUsersCreatedAfter(LocalDateTime date) {
        log.info("Buscando usuários criados após: {}", date);
        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt().isAfter(date))
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Atualiza informações do usuário
     */
    @Transactional
    public User update(Long id, UserRegistrationDTO dto) {
        log.info("Atualizando usuário ID: {}", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    Optional.ofNullable(dto.getNome())
                            .filter(nome -> !nome.isBlank())
                            .ifPresent(user::setNome);
                    
                    Optional.ofNullable(dto.getEmail())
                            .filter(email -> !email.isBlank())
                            .filter(email -> !existsByEmail(email) || email.equals(user.getEmail()))
                            .ifPresent(user::setEmail);
                    
                    Optional.ofNullable(dto.getTelefone())
                            .filter(telefone -> !telefone.isBlank())
                            .ifPresent(user::setTelefone);
                    
                    Optional.ofNullable(dto.getSenha())
                            .filter(senha -> !senha.isBlank())
                            .map(passwordEncoder::encode)
                            .ifPresent(user::setSenha);
                    
                    user.setUpdatedAt(LocalDateTime.now());
                    
                    User updatedUser = userRepository.save(user);
                    log.info("Usuário atualizado com sucesso: {}", updatedUser.getEmail());
                    return updatedUser;
                })
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com ID: {}", id);
                    return new RuntimeException("Usuário não encontrado");
                });
    }

    /**
     * Deleta usuário por ID
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando usuário ID: {}", id);
        
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> {
                            userRepository.delete(user);
                            log.info("Usuário deletado com sucesso: {}", user.getEmail());
                        },
                        () -> {
                            log.error("Usuário não encontrado com ID: {}", id);
                            throw new RuntimeException("Usuário não encontrado");
                        }
                );
    }

    /**
     * Valida credenciais de login
     */
    @Transactional(readOnly = true)
    public Optional<User> validateLogin(LoginDTO loginDTO) {
        log.info("Validando login para email: {}", loginDTO.getEmail());
        
        return userRepository.findByEmail(loginDTO.getEmail())
                .filter(user -> {
                    boolean matches = passwordEncoder.matches(loginDTO.getSenha(), user.getSenha());
                    if (matches) log.info("Login validado com sucesso para: {}", loginDTO.getEmail());
                     else log.warn("Senha inválida para email: {}", loginDTO.getEmail());
                     return matches;
                });
    }

    /**
     * Conta total de usuários
     */
    @Transactional(readOnly = true)
    public long count() {
        long count = userRepository.count();
        log.info("Total de usuários: {}", count);
        return count;
    }

    /**
     * Busca emails de todos os usuários
     */
    @Transactional(readOnly = true)
    public List<String> getAllEmails() {
        log.info("Buscando todos os emails");
        return userRepository.findAll().stream()
                .map(User::getEmail)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Valida se senha atende requisitos mínimos
     */
    protected boolean isValidPassword(String senha) {
        return Optional.ofNullable(senha)
                .filter(s -> s.length() >= 6)
                .filter(s -> s.length() <= 100)
                .isPresent();
    }

    /**
     * Encripta senha
     */
    protected String encryptPassword(String senha) {
        return Optional.ofNullable(senha)
                .filter(this::isValidPassword)
                .map(passwordEncoder::encode)
                .orElseThrow(() -> new IllegalArgumentException("Senha inválida"));
    }
}