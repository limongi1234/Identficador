package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.ClienteRegistroDTO;
import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Cliente registrarCliente(ClienteRegistroDTO dto) {
        log.info("Registrando novo cliente com email: {}", dto.email());

        if (clienteRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        if (dto.cpf() != null && !dto.cpf().trim().isEmpty() && clienteRepository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        
        Cliente cliente = new Cliente(
            dto.nome(),
            dto.email(),
            dto.telefone(),
            passwordEncoder.encode(dto.senha())
        );
        
        cliente.setCpf(dto.cpf());
        cliente.setEndereco(dto.endereco());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente registrado com sucesso: ID {}", clienteSalvo.getId());
        return clienteSalvo;
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
    
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.buscarPorNome(nome);
    }
    
    public List<Cliente> buscarClientesAtivos() {
        return clienteRepository.buscarClientesAtivos(1); // Pelo menos 1 entrega
    }
    
    public Cliente atualizarCliente(Long id, ClienteRegistroDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        // Verificar se email não está sendo usado por outro cliente
        if (!cliente.getEmail().equals(dto.email()) && clienteRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já está sendo usado por outro cliente");
        }
        
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setCpf(dto.cpf());
        cliente.setEndereco(dto.endereco());

        if (dto.senha() != null && !dto.senha().trim().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(dto.senha()));
        }
        
        return clienteRepository.save(cliente);
    }
}