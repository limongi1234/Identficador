package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.ClienteRegistroDTO;
import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clientes", description = "Gerenciamento completo de clientes do sistema")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(
            summary = "Registrar novo cliente",
            description = """
                Cria um novo cliente no sistema.
                
                **Validações automáticas:**
                - Email único
                - CPF único (se fornecido)
                - Senha criptografada automaticamente
                
                O cliente pode fazer login imediatamente após registro.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente registrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "Maria Santos",
                      "email": "maria@email.com",
                      "telefone": "(21) 98765-4321",
                      "cpf": "123.456.789-00",
                      "endereco": {
                        "logradouro": "Rua das Flores, 123",
                        "bairro": "Centro",
                        "cidade": "Rio de Janeiro",
                        "uf": "RJ"
                      }
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email ou CPF já cadastrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "Email já cadastrado",
                      "message": "Este email já está em uso"
                    }
                    """)
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> registrarCliente(@Valid @RequestBody ClienteRegistroDTO dto) {
        try {
            Cliente cliente = clienteService.registrarCliente(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", cliente.getId());
            response.put("nome", cliente.getNome());
            response.put("email", cliente.getEmail());
            response.put("telefone", cliente.getTelefone());
            response.put("cpf", cliente.getCpf());
            response.put("endereco", cliente.getEndereco());
            log.info("Cliente registrado: ID {}", cliente.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Erro ao registrar cliente: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna dados completos de um cliente específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Map<String, Object>> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", cliente.getId());
                    response.put("nome", cliente.getNome());
                    response.put("email", cliente.getEmail());
                    response.put("telefone", cliente.getTelefone());
                    response.put("cpf", cliente.getCpf());
                    response.put("endereco", cliente.getEndereco());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Buscar cliente por email",
            description = "Busca cliente pelo email exato"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Map<String, Object>> buscarPorEmail(@Parameter(description = "Email do cliente") @PathVariable String email) {
        return clienteService.buscarPorEmail(email)
                .map(cliente -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", cliente.getId());
                    response.put("nome", cliente.getNome());
                    response.put("email", cliente.getEmail());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca/{nome}")
    @Operation(
            summary = "Buscar clientes por nome",
            description = "Busca parcial por nome do cliente (LIKE %nome%)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes encontrados")
    })
    public ResponseEntity<List<Map<String, Object>>> buscarPorNome(@Parameter(description = "Parte do nome para busca") @PathVariable String nome) {
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        List<Map<String, Object>> response = clientes.stream()
                .map(cliente -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cliente.getId());
                    map.put("nome", cliente.getNome());
                    map.put("email", cliente.getEmail());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativos")
    @Operation(
            summary = "Listar clientes ativos",
            description = "Retorna clientes que já fizeram pelo menos 1 pedido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes ativos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> buscarClientesAtivos() {
        List<Cliente> clientes = clienteService.buscarClientesAtivos();
        List<Map<String, Object>> response = clientes.stream()
                .map(cliente -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cliente.getId());
                    map.put("nome", cliente.getNome());
                    map.put("email", cliente.getEmail());
                    map.put("telefone", cliente.getTelefone());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar dados do cliente",
            description = """
                Atualiza informações do cliente.
                
                **Observações:**
                - Senha só é alterada se fornecida
                - Valida email único (exceto o atual)
                - Só o próprio cliente ou ADMIN pode alterar
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Não autorizado")
    })
    public ResponseEntity<Map<String, Object>> atualizarCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody ClienteRegistroDTO dto) {
        try {
            Cliente cliente = clienteService.atualizarCliente(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", cliente.getId());
            response.put("nome", cliente.getNome());
            response.put("email", cliente.getEmail());
            response.put("telefone", cliente.getTelefone());
            response.put("cpf", cliente.getCpf());
            response.put("endereco", cliente.getEndereco());
            log.info("Cliente atualizado: ID {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar cliente {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
