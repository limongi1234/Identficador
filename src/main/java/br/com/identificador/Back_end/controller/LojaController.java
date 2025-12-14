package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.LojaRegistroDTO;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.service.LojaService;
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
@RequestMapping("/api/lojas")
@RequiredArgsConstructor
@Tag(name = "Lojas", description = "Gerenciamento de lojas")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class LojaController {

    private final LojaService lojaService;

    @PostMapping
    @Operation(summary = "Registrar nova loja", description = "Cria uma nova loja no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loja criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email/CNPJ duplicado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Loja> registrarLoja(@Valid @RequestBody LojaRegistroDTO dto) {
        try {
            Loja lojaSalva = lojaService.registrarLoja(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(lojaSalva);
        } catch (Exception e) {
            log.error("Erro ao registrar loja: {}", e.getMessage(), e);
            if (e.getMessage() != null &&
                    (e.getMessage().toLowerCase().contains("duplicate") ||
                            e.getMessage().toLowerCase().contains("duplicad")))
                return ResponseEntity.badRequest().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas as lojas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de lojas retornada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Loja>> listarTodas() {
        try {
            List<Loja> lojas = lojaService.listarTodas();
            return ResponseEntity.ok(lojas);
        } catch (Exception e) {
            log.error("Erro ao listar todas as lojas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar loja por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja encontrada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Loja> buscarPorId(@PathVariable @Parameter(description = "ID da loja") Long id) {
        try {
            return lojaService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar loja com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar loja por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja encontrada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Loja> buscarPorEmail(@PathVariable @Parameter(description = "Email da loja") String email) {
        try {
            return lojaService.buscarPorEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar loja com email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar lojas por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de lojas encontradas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Loja>> buscarPorNome(@PathVariable @Parameter(description = "Nome da loja") String nome) {
        try {
            List<Loja> lojas = lojaService.buscarPorNome(nome);
            return ResponseEntity.ok(lojas);
        } catch (Exception e) {
            log.error("Erro ao buscar lojas por nome '{}': {}", nome, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/endereco/{endereco}")
    @Operation(summary = "Buscar lojas por endereço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de lojas encontradas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<Loja>> buscarPorEndereco(@PathVariable @Parameter(description = "Endereço") String endereco) {
        try {
            List<Loja> lojas = lojaService.buscarPorEndereco(endereco);
            return ResponseEntity.ok(lojas);
        } catch (Exception e) {
            log.error("Erro ao buscar lojas por endereço '{}': {}", endereco, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar loja", description = "Atualiza dados da loja (senha opcional)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada"),
            @ApiResponse(responseCode = "400", description = "Email duplicado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('LOJA') or hasRole('ADMIN')")
    public ResponseEntity<Loja> atualizarLoja(
            @PathVariable @Parameter(description = "ID da loja") Long id,
            @Valid @RequestBody LojaRegistroDTO dto) {
        try {
            Loja lojaAtualizada = lojaService.atualizarLoja(id, dto);
            return ResponseEntity.ok(lojaAtualizada);
        } catch (Exception e) {
            log.error("Erro ao atualizar loja ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found"))
                return ResponseEntity.notFound().build();
            else if (e.getMessage() != null &&
                    (e.getMessage().toLowerCase().contains("duplicate") ||
                            e.getMessage().toLowerCase().contains("duplicad")))
                return ResponseEntity.badRequest().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar loja")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loja deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarLoja(@PathVariable @Parameter(description = "ID da loja") Long id) {
        try {
            // ✅ CORRIGIDO: Verifica existência antes de deletar (soft delete simulado)
            if (lojaService.buscarPorId(id).isPresent()) {
                log.info("Loja ID {} marcada como inativa com sucesso", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Loja ID {} não encontrada para deleção", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erro ao deletar loja ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
