package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.LojaRegistroDTO;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.repository.LojaRepository;
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
public class LojaService {
    
    private final LojaRepository lojaRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Loja registrarLoja(LojaRegistroDTO dto) {
        log.info("Registrando nova loja com email: {}", dto.getEmail());
        
        if (lojaRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email já cadastrado");

        if (lojaRepository.existsByCnpj(dto.getCnpj()))
            throw new RuntimeException("CNPJ já cadastrado");

        
        Loja loja = new Loja(
            dto.getNome(),
            dto.getEmail(),
            dto.getTelefone(),
            passwordEncoder.encode(dto.getSenha()),
            dto.getCnpj(),
            dto.getEndereco()
        );
        
        loja.setResponsavel(dto.getNomeResponsavel());
        loja.setHorarioFuncionamento(dto.getHorarioFuncionamento());
        
        Loja lojaSalva = lojaRepository.save(loja);
        log.info("Loja registrada com sucesso: ID {}", lojaSalva.getId());
        return lojaSalva;
    }
    
    public Optional<Loja> buscarPorId(Long id) {
        return lojaRepository.findById(id);
    }
    
    public Optional<Loja> buscarPorEmail(String email) {
        return lojaRepository.findByEmail(email);
    }
    
    public List<Loja> buscarPorNome(String nome) {
        return lojaRepository.buscarPorNome(nome);
    }
    
    public List<Loja> buscarPorEndereco(String endereco) {
        return lojaRepository.buscarPorEndereco(endereco);
    }
    
    public List<Loja> listarTodas() {
        return lojaRepository.findAll();
    }
    
    public Loja atualizarLoja(Long id, LojaRegistroDTO dto) {
        Loja loja = lojaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Loja não encontrada"));
        
        // Verificar se email não está sendo usado por outra loja
        if (!loja.getEmail().equals(dto.getEmail()) && lojaRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email já está sendo usado por outra loja");

        loja.setNome(dto.getNome());
        loja.setEmail(dto.getEmail());
        loja.setTelefone(dto.getTelefone());
        loja.setEndereco(dto.getEndereco());
        loja.setResponsavel(dto.getNomeResponsavel());
        loja.setHorarioFuncionamento(dto.getHorarioFuncionamento());
        
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty())
            loja.setSenha(passwordEncoder.encode(dto.getSenha()));

        return lojaRepository.save(loja);
    }
}