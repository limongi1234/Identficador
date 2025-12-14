package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.AtualizarStatusEntregaDTO;
import br.com.identificador.Back_end.dto.CriarEntregaDTO;
import br.com.identificador.Back_end.dto.EntregaDTO;
import br.com.identificador.Back_end.service.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
@Tag(name = "Entregas", description = "Gerenciamento completo de entregas")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class EntregaController {

    private final EntregaService entregaService;

    @PostMapping
    @Operation(summary = "Criar nova entrega")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrega criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou loja/cliente não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<EntregaDTO> criarEntrega(@Valid @RequestBody CriarEntregaDTO dto) {
        try {
            EntregaDTO entregaCriada = entregaService.criarEntrega(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entregaCriada);
        } catch (Exception e) {
            log.error("Erro ao criar entrega: {}", e.getMessage(), e);
            if (e.getMessage() != null &&
                    (e.getMessage().toLowerCase().contains("not found") ||
                            e.getMessage().toLowerCase().contains("inválid")))
                return ResponseEntity.badRequest().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{entregaId}/aceitar")
    @Operation(summary = "Aceitar entrega")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrega aceita"),
            @ApiResponse(responseCode = "400", description = "Entrega não disponível ou entregador ocupado"),
            @ApiResponse(responseCode = "404", description = "Entrega ou entregador não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<EntregaDTO> aceitarEntrega(
            @PathVariable @Parameter(description = "ID da entrega") Long entregaId,
            @RequestParam @Parameter(description = "ID do entregador") Long entregadorId) {
        try {
            EntregaDTO entregaAceita = entregaService.aceitarEntrega(entregaId, entregadorId);
            return ResponseEntity.ok(entregaAceita);
        } catch (Exception e) {
            log.error("Erro ao aceitar entrega {} por entregador {}: {}", entregaId, entregadorId, e.getMessage(), e);
            if (e.getMessage() != null &&
                    (e.getMessage().toLowerCase().contains("not found") ||
                            e.getMessage().toLowerCase().contains("ocupad") ||
                            e.getMessage().toLowerCase().contains("disponível")))
                return ResponseEntity.badRequest().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{entregaId}/status")
    @Operation(summary = "Atualizar status da entrega")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Entrega não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<EntregaDTO> atualizarStatus(
            @PathVariable @Parameter(description = "ID da entrega") Long entregaId,
            @Valid @RequestBody AtualizarStatusEntregaDTO dto) {
        try {
            EntregaDTO entregaAtualizada = entregaService.atualizarStatusEntrega(entregaId, dto);
            return ResponseEntity.ok(entregaAtualizada);
        } catch (Exception e) {
            log.error("Erro ao atualizar status da entrega {}: {}", entregaId, e.getMessage(), e);
            if (e.getMessage() != null &&
                    (e.getMessage().toLowerCase().contains("not found") ||
                            e.getMessage().toLowerCase().contains("inválid")))
                return ResponseEntity.notFound().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Listar entregas pendentes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de entregas pendentes"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<EntregaDTO>> listarPendentes() {
        try {
            List<EntregaDTO> pendentes = entregaService.buscarEntregasPendentes();
            return ResponseEntity.ok(pendentes);
        } catch (Exception e) {
            log.error("Erro ao listar entregas pendentes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entregador/{entregadorId}")
    @Operation(summary = "Entregas de um entregador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de entregas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<EntregaDTO>> listarPorEntregador(
            @PathVariable @Parameter(description = "ID do entregador") Long entregadorId) {
        try {
            List<EntregaDTO> entregas = entregaService.buscarEntregasDoEntregador(entregadorId);
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            log.error("Erro ao listar entregas do entregador {}: {}", entregadorId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/loja/{lojaId}")
    @Operation(summary = "Entregas de uma loja")
    @PreAuthorize("hasRole('LOJA')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de entregas da loja"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<EntregaDTO>> listarPorLoja(
            @PathVariable @Parameter(description = "ID da loja") Long lojaId) {
        try {
            List<EntregaDTO> entregas = entregaService.buscarEntregasDaLoja(lojaId);
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            log.error("Erro ao listar entregas da loja {}: {}", lojaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Entregas de um cliente")
    @PreAuthorize("hasRole('CLIENTE')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de entregas do cliente"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<EntregaDTO>> listarPorCliente(
            @PathVariable @Parameter(description = "ID do cliente") Long clienteId) {
        try {
            List<EntregaDTO> entregas = entregaService.buscarEntregasDoCliente(clienteId);
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            log.error("Erro ao listar entregas do cliente {}: {}", clienteId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
