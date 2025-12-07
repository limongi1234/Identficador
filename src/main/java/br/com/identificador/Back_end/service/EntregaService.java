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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final EntregadorRepository entregadorRepository;
    private final LojaRepository lojaRepository;
    private final ClienteRepository clienteRepository;

    public EntregaDTO criarEntrega(CriarEntregaDTO dto) {
        log.info("Criando nova entrega para loja ID: {} e cliente ID: {}", dto.getLojaId(), dto.getClienteId());

        Loja loja = lojaRepository.findById(dto.getLojaId())
                .orElseThrow(() -> new RuntimeException("Loja não encontrada"));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Entrega entrega = new Entrega();
        entrega.setLoja(loja);
        entrega.setCliente(cliente);
        entrega.setEnderecoOrigem(dto.getEnderecoOrigem());
        entrega.setEnderecoDestino(dto.getEnderecoDestino());
        entrega.setProdutoDescricao(dto.getDescricaoProduto());
        entrega.setValorEntrega(dto.getValorEntrega());
        entrega.setValorGorjeta(dto.getValorGorjeta());
        entrega.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());
        entrega.setObservacoes(dto.getObservacoes());
        entrega.setStatusEntrega(StatusEntrega.PENDENTE);

        Entrega entregaSalva = entregaRepository.save(entrega);
        log.info("Entrega criada com sucesso: ID {}", entregaSalva.getId());

        return converterParaDTO(entregaSalva);
    }

    public EntregaDTO aceitarEntrega(Long entregaId, Long entregadorId) {
        log.info("Entregador ID {} tentando aceitar entrega ID {}", entregadorId, entregaId);

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega não encontrada"));

        if (entrega.getStatusEntrega() != StatusEntrega.PENDENTE)
            throw new RuntimeException("Esta entrega não está mais disponível");

        Entregador entregador = entregadorRepository.findById(entregadorId)
                .orElseThrow(() -> new RuntimeException("Entregador não encontrado"));

        if (entregador.getStatus() != StatusEntregador.ONLINE)
            throw new RuntimeException("Entregador deve estar online para aceitar entregas");

        // Verificar se o entregador já tem entregas em andamento
        List<Entrega> entregasEmAndamento = entregaRepository.buscarEntregasEmAndamento(entregador);
        if (!entregasEmAndamento.isEmpty())
            throw new RuntimeException("Entregador já possui entregas em andamento");

        entrega.setEntregador(entregador);
        entrega.setStatusEntrega(StatusEntrega.ACEITA);
        entrega.setIniciadoEm(LocalDateTime.now());

        // Atualizar status do entregador
        entregador.setStatus(StatusEntregador.EM_ENTREGA);
        entregadorRepository.save(entregador);

        Entrega entregaAtualizada = entregaRepository.save(entrega);
        log.info("Entrega aceita com sucesso pelo entregador: {}", entregador.getNome());

        return converterParaDTO(entregaAtualizada);
    }

    public EntregaDTO atualizarStatusEntrega(Long entregaId, AtualizarStatusEntregaDTO dto) {
        log.info("Atualizando status da entrega ID {} para: {}", entregaId, dto.getNovoStatus());

        Entrega entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega não encontrada"));

        StatusEntrega statusAnterior = entrega.getStatusEntrega();
        entrega.setStatusEntrega(dto.getNovoStatus());

        // Atualizar timestamps específicos
        switch (dto.getNovoStatus()) {
            case EM_ANDAMENTO:
                entrega.setIniciadoEm(LocalDateTime.now());
                break;
            case ENTREGUE:
                entrega.setFinalizadoEm(LocalDateTime.now());
                atualizarEstatisticasEntregador(entrega.getEntregador());
                entrega.getEntregador().setStatus(StatusEntregador.ONLINE);
                entregadorRepository.save(entrega.getEntregador());
                break;
            case CANCELADA:
            case REJEITADA:
                entrega.setCanceladoEm(LocalDateTime.now());
                if (entrega.getEntregador() != null) {
                    entrega.getEntregador().setStatus(StatusEntregador.ONLINE);
                    entregadorRepository.save(entrega.getEntregador());
                }
                break;
        }

        if (dto.getObservacoes() != null) entrega.setObservacoes(dto.getObservacoes());

        Entrega entregaAtualizada = entregaRepository.save(entrega);
        log.info("Status da entrega atualizado de {} para {}", statusAnterior, dto.getNovoStatus());

        return converterParaDTO(entregaAtualizada);
    }

    private void atualizarEstatisticasEntregador(Entregador entregador) {
        // Implementar lógica de atualização de estatísticas
        // Por exemplo: incrementar contador de entregas, calcular avaliação média, etc.
    }

    public List<EntregaDTO> buscarEntregasPendentes() {
        log.debug("Buscando entregas pendentes");
        return entregaRepository.buscarEntregasPendentes()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<EntregaDTO> buscarEntregasDoEntregador(Long entregadorId) {
        Entregador entregador = entregadorRepository.findById(entregadorId)
                .orElseThrow(() -> new RuntimeException("Entregador não encontrado"));

        return entregaRepository.findByEntregadorOrderByCriadoEmDesc(entregador)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<EntregaDTO> buscarEntregasDaLoja(Long lojaId) {
        Loja loja = lojaRepository.findById(lojaId)
                .orElseThrow(() -> new RuntimeException("Loja não encontrada"));

        return entregaRepository.findByLojaOrderByCriadoEmDesc(loja)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<EntregaDTO> buscarEntregasDoCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return entregaRepository.findByClienteOrderByCriadoEmDesc(cliente)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private EntregaDTO converterParaDTO(Entrega entrega) {
        EntregaDTO dto = new EntregaDTO();
        dto.setId(entrega.getId());
        dto.setLojaId(entrega.getLoja().getId());
        dto.setLojaNome(entrega.getLoja().getNome());
        dto.setClienteId(entrega.getCliente().getId());
        dto.setClienteNome(entrega.getCliente().getNome());

        if (entrega.getEntregador() != null) {
            dto.setEntregadorId(entrega.getEntregador().getId());
            dto.setEntregadorNome(entrega.getEntregador().getNome());
        }

        dto.setEnderecoOrigem(entrega.getEnderecoOrigem());
        dto.setEnderecoDestino(entrega.getEnderecoDestino());
        dto.setValorEntrega(entrega.getValorEntrega());
        dto.setValorGorjeta(entrega.getValorGorjeta());
        dto.setTempoEstimadoMinutos(entrega.getTempoEstimadoMinutos());
        dto.setObservacoes(entrega.getObservacoes());
        dto.setStatusEntrega(entrega.getStatusEntrega());
        dto.setCriadoEm(entrega.getCriadoEm());
        dto.setIniciadoEm(entrega.getIniciadoEm());
        dto.setFinalizadoEm(entrega.getFinalizadoEm());
        dto.setCanceladoEm(entrega.getCanceladoEm());

        return dto;
    }
}