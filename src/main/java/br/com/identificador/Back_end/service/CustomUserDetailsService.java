package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.repository.ClienteRepository;
import br.com.identificador.Back_end.repository.EntregadorRepository;
import br.com.identificador.Back_end.repository.LojaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final EntregadorRepository entregadorRepository;
    private final ClienteRepository clienteRepository;
    private final LojaRepository lojaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Tentando carregar usuário com email: {}", email);

        // Busca em todas as tabelas (Entregador, Cliente, Loja)
        User user = findUserByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com email: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado: " + email);
                });

        log.info("Usuário encontrado: {} (Tipo: {})", user.getEmail(), user.getClass().getSimpleName());

        // Define a role baseada no tipo de usuário
        String role = determineRole(user);
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getSenha())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private Optional<User> findUserByEmail(String email) {
        // Tenta encontrar como Entregador
        Optional<Entregador> entregador = entregadorRepository.findByEmail(email);
        if (entregador.isPresent()) {
            log.debug("Usuário encontrado como Entregador");
            return Optional.of(entregador.get());
        }

        // Tenta encontrar como Cliente
        Optional<Cliente> cliente = clienteRepository.findByEmail(email);
        if (cliente.isPresent()) {
            log.debug("Usuário encontrado como Cliente");
            return Optional.of(cliente.get());
        }

        // Tenta encontrar como Loja
        Optional<Loja> loja = lojaRepository.findByEmail(email);
        if (loja.isPresent()) {
            log.debug("Usuário encontrado como Loja");
            return Optional.of(loja.get());
        }

        log.warn("Usuário não encontrado em nenhuma tabela: {}", email);
        return Optional.empty();
    }

    private String determineRole(User user) {
        if (user instanceof Entregador) {
            return "ROLE_ENTREGADOR";
        } else if (user instanceof Cliente) {
            return "ROLE_CLIENTE";
        } else if (user instanceof Loja) {
            return "ROLE_LOJA";
        }
        return "ROLE_USER";
    }

    /**
     * Busca usuário completo por email (retorna o objeto User com todos os dados)
     */
    public Optional<User> findCompleteUserByEmail(String email) {
        return findUserByEmail(email);
    }
}