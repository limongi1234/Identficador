package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.AtualizarStatusEntregaDTO;
import br.com.identificador.Back_end.dto.CriarEntregaDTO;
import br.com.identificador.Back_end.dto.EntregaDTO;
import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.model.Entrega;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import br.com.identificador.Back_end.repository.ClienteRepository;
import br.com.identificador.Back_end.repository.EntregaRepository;
import br.com.identificador.Back_end.repository.EntregadorRepository;
import br.com.identificador.Back_end.repository.LojaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final EntregadorRepository entregadorRepository;
    private final LojaRepository lojaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public EntregaDTO criarEntrega(CriarEntregaDTO dto) {
        log.info("Criando entrega - Loja: {}, Cliente: {}", dto.lojaId(), dto.clienteId());

        Loja loja = lojaRepository.findById(dto.lojaId())
                .orElseThrow(() -> new RuntimeException("Loja não encontrada: " + dto.lojaId()));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + dto.clienteId()));

        Entrega entrega = new Entrega();
        entrega.setLoja(loja);
        entrega.setCliente(cliente);
        entrega.setEnderecoOrigem(dto.enderecoOrigem());
        entrega.setEnderecoDestino(dto.enderecoDestino());
        entrega.setProdutoDescricao(dto.descricaoProduto());
        entrega.setValorEntrega(dto.valorEntrega());
        entrega.setValorGorjeta(dto.valorGorjeta() != null ? dto.valorGorjeta() : BigDecimal.ZERO);
        entrega.setTempoEstimadoMinutos(dto.tempoEstimadoMinutos());
        entrega.setObservacoes(dto.observacoes());
        entrega.setStatusEntrega(StatusEntrega.A_CAMINHO_COLETA);
        entrega.setCriadoEm(LocalDateTime.now());

        Entrega saved = entregaRepository.save(entrega);
        log.info("✅ Entrega criada ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public EntregaDTO aceitarEntrega(Long entregaId, Long entregadorId) {
        log.info("Entregador {} aceitando entrega {}", entregadorId, entregaId);

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega não encontrada: " + entregaId));

        if (entrega.getStatusEntrega() != StatusEntrega.A_CAMINHO_COLETA)
            throw new RuntimeException("Entrega não disponível para aceitação");

        Entregador entregador = entregadorRepository.findById(entregadorId)
                .orElseThrow(() -> new RuntimeException("Entregador não encontrado: " + entregadorId));

        if (entregador.getStatus() != StatusEntregador.DISPONIVEL)
            throw new RuntimeException("Entregador deve estar disponível");

        // Verifica se entregador já tem entregas ativas
        boolean temEntregaAtiva = entregaRepository.findAll().stream()
                .filter(e -> e.getEntregador() != null
                        && e.getEntregador().getId().equals(entregadorId)
                        && (e.getStatusEntrega() == StatusEntrega.COLETANDO
                        || e.getStatusEntrega() == StatusEntrega.A_CAMINHO_ENTREGA
                        || e.getStatusEntrega() == StatusEntrega.CHEGOU_DESTINO))
                .findAny().isPresent();

        if (temEntregaAtiva)
            throw new RuntimeException("Entregador já tem entregas em andamento");

        entrega.setEntregador(entregador);
        entrega.setStatusEntrega(StatusEntrega.COLETANDO);
        entrega.setIniciadoEm(LocalDateTime.now());

        entregador.setStatus(StatusEntregador.DISPONIVEL);
        entregadorRepository.save(entregador);
        Entrega saved = entregaRepository.save(entrega);

        log.info("✅ Entrega {} aceita por {}", entregaId, entregador.getNome());
        return toDTO(saved);
    }

    @Transactional
    public EntregaDTO atualizarStatusEntrega(Long entregaId, AtualizarStatusEntregaDTO dto) {
        log.info("Atualizando status entrega {} para {}", entregaId, dto.novoStatus());

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega não encontrada: " + entregaId));

        StatusEntrega statusAnterior = entrega.getStatusEntrega();
        entrega.setStatusEntrega(dto.novoStatus());

        switch (dto.novoStatus()) {
            case COLETANDO:
                entrega.setIniciadoEm(LocalDateTime.now());
                break;
            case ENTREGUE:
                entrega.setFinalizadoEm(LocalDateTime.now());
                if (entrega.getEntregador() != null) {
                    atualizarEstatisticasEntregador(entrega.getEntregador());
                    entrega.getEntregador().setStatus(StatusEntregador.DISPONIVEL);
                    entregadorRepository.save(entrega.getEntregador());
                }
                break;
            case CANCELADA:
            case PROBLEMA:
                entrega.setCanceladoEm(LocalDateTime.now());
                if (entrega.getEntregador() != null) {
                    entrega.getEntregador().setStatus(StatusEntregador.DISPONIVEL);
                    entregadorRepository.save(entrega.getEntregador());
                }
                break;
        }

        if (dto.observacoes() != null && !dto.observacoes().trim().isEmpty()) {
            String obs = dto.observacoes();
            if (entrega.getObservacoes() != null)
                obs = entrega.getObservacoes() + "\n" + dto.observacoes();

            entrega.setObservacoes(obs);
        }

        Entrega saved = entregaRepository.save(entrega);
        log.info("✅ Status: {} → {}", statusAnterior, dto.novoStatus());
        return toDTO(saved);
    }

    private void atualizarEstatisticasEntregador(Entregador entregador) {
        entregador.setTotalEntregas((entregador.getTotalEntregas() == null ? 0 : entregador.getTotalEntregas()) + 1);
        entregadorRepository.save(entregador);
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> buscarEntregasPendentes() {
        return entregaRepository.findAll().stream()
                .filter(e -> e.getStatusEntrega() == StatusEntrega.A_CAMINHO_COLETA)
                .limit(50)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> buscarEntregasDoEntregador(Long entregadorId) {
        return entregaRepository.findAll().stream()
                .filter(e -> e.getEntregador() != null && e.getEntregador().getId().equals(entregadorId))
                .limit(100)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> buscarEntregasDaLoja(Long lojaId) {
        return entregaRepository.findAll().stream()
                .filter(e -> e.getLoja().getId().equals(lojaId))
                .limit(100)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> buscarEntregasDoCliente(Long clienteId) {
        return entregaRepository.findAll().stream()
                .filter(e -> e.getCliente().getId().equals(clienteId))
                .limit(100)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EntregaDTO toDTO(Entrega entrega) {
        return new EntregaDTO(
                entrega.getId(),
                entrega.getEntregador() != null ? entrega.getEntregador().getId() : null,
                entrega.getEntregador() != null ? entrega.getEntregador().getNome() : null,
                entrega.getLoja().getId(),
                entrega.getLoja().getNome(),
                entrega.getCliente().getId(),
                entrega.getCliente().getNome(),
                entrega.getEnderecoOrigem(),
                entrega.getEnderecoDestino(),
                entrega.getProdutoDescricao(),
                entrega.getStatusEntrega(),
                entrega.getValorEntrega(),
                entrega.getValorGorjeta(),
                entrega.getValorEntrega().add(entrega.getValorGorjeta() != null ? entrega.getValorGorjeta() : BigDecimal.ZERO),
                entrega.getTempoEstimadoMinutos(),
                entrega.getObservacoes(),
                entrega.getCriadoEm(),
                entrega.getIniciadoEm(),
                entrega.getFinalizadoEm(),
                entrega.getCanceladoEm()
        );
    }
}
