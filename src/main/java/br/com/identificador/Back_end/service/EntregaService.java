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
        log.info("Criando entrega - Loja: {}, Cliente: {}", dto.getLojaId(), dto.getClienteId());

        Loja loja = lojaRepository.findById(dto.getLojaId())
                .orElseThrow(() -> new RuntimeException("Loja não encontrada: " + dto.getLojaId()));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + dto.getClienteId()));

        Entrega entrega = new Entrega();
        entrega.setLoja(loja);
        entrega.setCliente(cliente);
        entrega.setEnderecoOrigem(dto.getEnderecoOrigem());
        entrega.setEnderecoDestino(dto.getEnderecoDestino());
        entrega.setProdutoDescricao(dto.getDescricaoProduto());
        entrega.setValorEntrega(dto.getValorEntrega());
        entrega.setValorGorjeta(dto.getValorGorjeta() != null ? dto.getValorGorjeta() : BigDecimal.ZERO);
        entrega.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());
        entrega.setObservacoes(dto.getObservacoes());
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
        log.info("Atualizando status entrega {} para {}", entregaId, dto.getNovoStatus());

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega não encontrada: " + entregaId));

        StatusEntrega statusAnterior = entrega.getStatusEntrega();
        entrega.setStatusEntrega(dto.getNovoStatus());

        switch (dto.getNovoStatus()) {
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

        if (dto.getObservacoes() != null && !dto.getObservacoes().trim().isEmpty()) {
            String obs = dto.getObservacoes();
            if (entrega.getObservacoes() != null)
                obs = entrega.getObservacoes() + "\n" + dto.getObservacoes();

            entrega.setObservacoes(obs);
        }

        Entrega saved = entregaRepository.save(entrega);
        log.info("✅ Status: {} → {}", statusAnterior, dto.getNovoStatus());
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
        return EntregaDTO.builder()
                .id(entrega.getId())
                .entregadorId(entrega.getEntregador() != null ? entrega.getEntregador().getId() : null)
                .nomeEntregador(entrega.getEntregador() != null ? entrega.getEntregador().getNome() : null)
                .lojaId(entrega.getLoja().getId())
                .nomeLoja(entrega.getLoja().getNome())
                .clienteId(entrega.getCliente().getId())
                .nomeCliente(entrega.getCliente().getNome())
                .enderecoOrigem(entrega.getEnderecoOrigem())
                .enderecoDestino(entrega.getEnderecoDestino())
                .descricaoProduto(entrega.getProdutoDescricao())
                .statusEntrega(entrega.getStatusEntrega())
                .valorEntrega(entrega.getValorEntrega())
                .valorGorjeta(entrega.getValorGorjeta())
                .tempoEstimadoMinutos(entrega.getTempoEstimadoMinutos())
                .observacoes(entrega.getObservacoes())
                .criadoEm(entrega.getCriadoEm())
                .iniciadoEm(entrega.getIniciadoEm())
                .finalizadoEm(entrega.getFinalizadoEm())
                .canceladoEm(entrega.getCanceladoEm())
                .build();
    }
}
