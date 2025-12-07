package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.EntregadorRegistroDTO;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.enuns.Aplicativo;

import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import br.com.identificador.Back_end.repository.EntregadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntregadorService {

    private final EntregadorRepository entregadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Entregador registrarEntregador(EntregadorRegistroDTO dto) {
        // Validações usando Streams
        validarEmailUnico(dto.getEmail());
        validarCpfUnico(dto.getCpf());

        // Criar entregador
        Entregador entregador = new Entregador(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                passwordEncoder.encode(dto.getSenha()),
                dto.getCpf(),
                dto.getRg(),
                dto.getCnh()
        );

        // Gerar QR Code UUID único
        entregador.setQrCodeUuid(UUID.randomUUID().toString());

        // Adicionar aplicativos usando Optional e Stream
        Set<Aplicativo> aplicativos = dto.getAplicativos() != null 
            ? dto.getAplicativos().stream()
                .collect(Collectors.toSet())
            : Set.of();
        entregador.setAplicativos(aplicativos);

        return entregadorRepository.save(entregador);
    }

    @Transactional(readOnly = true)
    public Entregador buscarPorId(Long id) {
        return entregadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Entregador buscarPorEmail(String email) {
        return entregadorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado com email: " + email));
    }

    @Transactional(readOnly = true)
    public Entregador buscarPorQrCode(String qrCodeUuid) {
        return entregadorRepository.findByQrCodeUuid(qrCodeUuid)
                .orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado com QR Code: " + qrCodeUuid));
    }

    @Transactional(readOnly = true)
    public Entregador buscarPorCpf(String cpf) {
        return entregadorRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Entregador não encontrado com CPF: " + cpf));
    }

    @Transactional(readOnly = true)
    public List<Entregador> listarTodos() {
        return entregadorRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarPorStatus(StatusEntregador status) {
        return entregadorRepository.findByStatus(status).stream()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarDisponiveis() {
        return entregadorRepository.buscarEntregadoresDisponiveis(StatusEntregador.DISPONIVEL).stream()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarPorAvaliacaoMinima(Double avaliacaoMinima) {
        return entregadorRepository.buscarPorAvaliacaoMinima(avaliacaoMinima).stream()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarMelhoresAvaliados(int limite) {
        return entregadorRepository.findAll().stream()
                .sorted((e1, e2) -> Double.compare(e2.getAvaliacaoMedia(), e1.getAvaliacaoMedia()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarPorAplicativo(Aplicativo aplicativo) {
        return entregadorRepository.findAll().stream()
                .filter(e -> e.getAplicativos().contains(aplicativo))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarExperientes(int minimoEntregas) {
        return entregadorRepository.findAll().stream()
                .filter(e -> e.getTotalEntregas() >= minimoEntregas)
                .sorted((e1, e2) -> Integer.compare(e2.getTotalEntregas(), e1.getTotalEntregas()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double calcularAvaliacaoMediaGeral() {
        return entregadorRepository.findAll().stream()
                .filter(e -> e.getTotalEntregas() > 0)
                .mapToDouble(Entregador::getAvaliacaoMedia)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public long contarPorStatus(StatusEntregador status) {
        return entregadorRepository.findAll().stream()
                .filter(e -> e.getStatus() == status)
                .count();
    }

    @Transactional(readOnly = true)
    public List<Entregador> buscarComMultiplosAplicativos(int minimoAplicativos) {
        return entregadorRepository.findAll().stream()
                .filter(e -> e.getAplicativos().size() >= minimoAplicativos)
                .collect(Collectors.toList());
    }

    @Transactional
    public Entregador atualizarStatus(Long id, StatusEntregador novoStatus) {
        Entregador entregador = buscarPorId(id);
        entregador.setStatus(novoStatus);
        return entregadorRepository.save(entregador);
    }

    @Transactional
    public Entregador adicionarAplicativo(Long id, Aplicativo aplicativo) {
        Entregador entregador = buscarPorId(id);
        entregador.getAplicativos().add(aplicativo);
        return entregadorRepository.save(entregador);
    }

    @Transactional
    public Entregador removerAplicativo(Long id, Aplicativo aplicativo) {
        Entregador entregador = buscarPorId(id);
        entregador.getAplicativos().remove(aplicativo);
        return entregadorRepository.save(entregador);
    }

    @Transactional
    public Entregador atualizarAplicativos(Long id, Set<Aplicativo> aplicativos) {
        Entregador entregador = buscarPorId(id);
        Set<Aplicativo> aplicativosValidados = aplicativos.stream()
                .filter(app -> app != null)
                .collect(Collectors.toSet());
        entregador.setAplicativos(aplicativosValidados);
        return entregadorRepository.save(entregador);
    }

    @Transactional
    public Entregador atualizarAvaliacao(Long id, Double novaAvaliacao) {
        Entregador entregador = buscarPorId(id);
        
        // Cálculo da média ponderada usando stream
        Integer totalEntregas = entregador.getTotalEntregas();
        Double avaliacaoAtual = entregador.getAvaliacaoMedia();
        
        Double novaMedia = ((avaliacaoAtual * totalEntregas) + novaAvaliacao) / (totalEntregas + 1);
        
        entregador.setAvaliacaoMedia(Math.round(novaMedia * 100.0) / 100.0); // 2 casas decimais
        entregador.setTotalEntregas(totalEntregas + 1);
        
        return entregadorRepository.save(entregador);
    }

    @Transactional
    public List<Entregador> atualizarStatusEmLote(List<Long> ids, StatusEntregador novoStatus) {
        return ids.stream()
                .map(this::buscarPorId)
                .peek(entregador -> entregador.setStatus(novoStatus))
                .map(entregadorRepository::save)
                .collect(Collectors.toList());
    }

    @Transactional
    public Entregador atualizarDados(Long id, EntregadorRegistroDTO dto) {
        Entregador entregador = buscarPorId(id);

        // Verificar se email já existe em outro entregador
        if (!entregador.getEmail().equals(dto.getEmail())) validarEmailUnico(dto.getEmail());

        // Verificar se CPF já existe em outro entregador
        if (!entregador.getCpf().equals(dto.getCpf())) validarCpfUnico(dto.getCpf());

        // Atualizar dados
        entregador.setNome(dto.getNome());
        entregador.setEmail(dto.getEmail());
        entregador.setTelefone(dto.getTelefone());
        entregador.setCpf(dto.getCpf());
        entregador.setRg(dto.getRg());
        entregador.setCnh(dto.getCnh());

        // Atualizar senha apenas se fornecida usando Optional
        if (dto.getSenha() != null && !dto.getSenha().isBlank())
            entregador.setSenha(passwordEncoder.encode(dto.getSenha()));


        // Atualizar aplicativos usando Stream
        if (dto.getAplicativos() != null) {
            Set<Aplicativo> aplicativosValidados = dto.getAplicativos().stream()
                    .filter(app -> app != null)
                    .collect(Collectors.toSet());
            entregador.setAplicativos(aplicativosValidados);
        }

        return entregadorRepository.save(entregador);
    }

    @Transactional
    public void deletarEntregador(Long id) {
        if (!entregadorRepository.existsById(id))
            throw new IllegalArgumentException("Entregador não encontrado com ID: " + id);

        entregadorRepository.deleteById(id);
    }

    @Transactional
    public void deletarEntregadoresEmLote(List<Long> ids) {
        ids.stream()
                .peek(id -> {
                    if (!entregadorRepository.existsById(id))
                        throw new IllegalArgumentException("Entregador não encontrado com ID: " + id);

                })
                .forEach(entregadorRepository::deleteById);
    }

    @Transactional
    public String regenerarQrCode(Long id) {
        Entregador entregador = buscarPorId(id);
        String novoQrCodeUuid = UUID.randomUUID().toString();
        entregador.setQrCodeUuid(novoQrCodeUuid);
        entregadorRepository.save(entregador);
        return novoQrCodeUuid;
    }

    // Métodos auxiliares de validação
    private void validarEmailUnico(String email) {
        if (entregadorRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email já cadastrado");
    }

    private void validarCpfUnico(String cpf) {
        if (entregadorRepository.existsByCpf(cpf))
            throw new IllegalArgumentException("CPF já cadastrado");
    }
}