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
        log.info("Registrando novo cliente com email: {}", dto.getEmail());
        
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        if (dto.getCpf() != null && !dto.getCpf().trim().isEmpty() && clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        
        Cliente cliente = new Cliente(
            dto.getNome(),
            dto.getEmail(),
            dto.getTelefone(),
            passwordEncoder.encode(dto.getSenha())
        );
        
        cliente.setCpf(dto.getCpf());
        cliente.setEndereco(dto.getEndereco());
        
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
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já está sendo usado por outro cliente");
        }
        
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCpf(dto.getCpf());
        cliente.setEndereco(dto.getEndereco());
        
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        
        return clienteRepository.save(cliente);
    }
}