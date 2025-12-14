package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.EntregadorRegistroDTO;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import br.com.identificador.Back_end.service.EntregadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/entregadores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Entregadores", description = "Gerenciamento completo de entregadores com QR Code e status")
public class EntregadorController {

    private final EntregadorService entregadorService;

    @PostMapping
    @Operation(
            summary = "Registrar novo entregador",
            description = """
                Cria novo entregador com QR Code único e aplicativos.
                
                **Validações:**
                - Email e CPF únicos
                - Senha criptografada automaticamente
                - QR Code UUID gerado automaticamente
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Entregador registrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "João Silva",
                      "email": "joao@entrega.com",
                      "telefone": "(21) 98765-4321",
                      "cpf": "123.456.789-00",
                      "qrCodeUuid": "550e8400-e29b-41d4-a716-446655440000",
                      "aplicativos": ["IFOOD", "UBER_EATS"],
                      "status": "DISPONIVEL"
                    }
                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<Map<String, Object>> registrarEntregador(@Valid @RequestBody EntregadorRegistroDTO dto) {
        try {
            Entregador entregador = entregadorService.registrarEntregador(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", entregador.getId());
            response.put("nome", entregador.getNome());
            response.put("email", entregador.getEmail());
            response.put("qrCodeUuid", entregador.getQrCodeUuid());
            response.put("aplicativos", entregador.getAplicativos());
            log.info("Entregador registrado: ID {}", entregador.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao registrar: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar entregador por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entregador encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public ResponseEntity<Map<String, Object>> buscarPorId(@Parameter(description = "ID do entregador") @PathVariable Long id) {
        try {
            Entregador entregador = entregadorService.buscarPorId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("id", entregador.getId());
            response.put("nome", entregador.getNome());
            response.put("email", entregador.getEmail());
            response.put("status", entregador.getStatus());
            response.put("qrCodeUuid", entregador.getQrCodeUuid());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/qr/{qrCode}")
    @Operation(summary = "Buscar por QR Code", description = "Validação de QR Code para app mobile")
    public ResponseEntity<Map<String, Object>> buscarPorQrCode(@Parameter(description = "UUID do QR Code") @PathVariable String qrCode) {
        try {
            Entregador entregador = entregadorService.buscarPorQrCode(qrCode);
            Map<String, Object> response = new HashMap<>();
            response.put("id", entregador.getId());
            response.put("nome", entregador.getNome());
            response.put("status", entregador.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar entregadores disponíveis")
    @PreAuthorize("hasRole('LOJA') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listarDisponiveis() {
        List<Entregador> entregadores = entregadorService.buscarDisponiveis();
        List<Map<String, Object>> response = entregadores.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", e.getId());
                    map.put("nome", e.getNome());
                    map.put("avaliacao", e.getAvaliacaoMedia());
                    map.put("totalEntregas", e.getTotalEntregas());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/melhores/{limite}")
    @Operation(summary = "Melhores avaliados", description = "Top N entregadores por avaliação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista dos melhores"),
            @ApiResponse(responseCode = "400", description = "Limite inválido")
    })
    public ResponseEntity<List<Map<String, Object>>> melhoresAvaliados(
            @Parameter(description = "Quantidade máxima (1-50)") @PathVariable int limite) {
        if (limite < 1 || limite > 50) return ResponseEntity.badRequest().build();

        List<Entregador> entregadores = entregadorService.buscarMelhoresAvaliados(limite);
        List<Map<String, Object>> response = entregadores.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", e.getId());
                    map.put("nome", e.getNome());
                    map.put("avaliacao", e.getAvaliacaoMedia());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status/{status}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @Operation(summary = "Atualizar status do entregador")
    public ResponseEntity<Map<String, Object>> atualizarStatus(
            @Parameter(description = "ID do entregador") @PathVariable Long id,
            @Parameter(description = "Novo status") @PathVariable StatusEntregador status) {
        try {
            Entregador entregador = entregadorService.atualizarStatus(id, status);
            return ResponseEntity.ok(Map.of("id", entregador.getId(), "status", entregador.getStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/avaliacao/{nota}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('LOJA')")
    @Operation(summary = "Avaliar entregador", description = "Nota de 1.0 a 5.0")
    public ResponseEntity<Map<String, Object>> avaliarEntregador(
            @Parameter(description = "ID do entregador") @PathVariable Long id,
            @Parameter(description = "Nota (1.0-5.0)") @PathVariable Double nota) {
        if (nota < 1.0 || nota > 5.0)
            return ResponseEntity.badRequest().body(Map.of("error", "Nota deve estar entre 1.0 e 5.0"));

        try {
            Entregador entregador = entregadorService.atualizarAvaliacao(id, nota);
            return ResponseEntity.ok(Map.of(
                    "id", entregador.getId(),
                    "novaAvaliacao", entregador.getAvaliacaoMedia(),
                    "totalEntregas", entregador.getTotalEntregas()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ENTREGADOR') or hasRole('ADMIN')")
    @Operation(summary = "Atualizar dados do entregador")
    public ResponseEntity<Map<String, Object>> atualizarDados(
            @Parameter(description = "ID do entregador") @PathVariable Long id,
            @Valid @RequestBody EntregadorRegistroDTO dto) {
        try {
            Entregador entregador = entregadorService.atualizarDados(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", entregador.getId());
            response.put("nome", entregador.getNome());
            response.put("email", entregador.getEmail());
            response.put("qrCodeUuid", entregador.getQrCodeUuid());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar entregador")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do entregador") @PathVariable Long id) {
        try {
            entregadorService.deletarEntregador(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/qr/regenerar/{id}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ENTREGADOR')")
    @Operation(summary = "Regenerar QR Code")
    public ResponseEntity<Map<String, String>> regenerarQrCode(@PathVariable Long id) {
        try {
            String novoQrCode = entregadorService.regenerarQrCode(id);
            return ResponseEntity.ok(Map.of("novoQrCodeUuid", novoQrCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
