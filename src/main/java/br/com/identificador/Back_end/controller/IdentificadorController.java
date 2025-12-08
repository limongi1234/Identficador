package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.ClienteRegistroDTO;
import br.com.identificador.Back_end.dto.EntregadorRegistroDTO;
import br.com.identificador.Back_end.dto.LojaRegistroDTO;
import br.com.identificador.Back_end.dto.AtualizarStatusEntregaDTO;
import br.com.identificador.Back_end.dto.CriarEntregaDTO;
import br.com.identificador.Back_end.dto.EntregaDTO;
import br.com.identificador.Back_end.dto.MensagemChat;
import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.model.Loja;
import br.com.identificador.Back_end.model.QRCode;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import br.com.identificador.Back_end.service.EntregadorService;
import br.com.identificador.Back_end.service.ClienteService;
import br.com.identificador.Back_end.service.LojaService;
import br.com.identificador.Back_end.service.EntregaService;
import br.com.identificador.Back_end.service.QRCodeService;
import br.com.identificador.Back_end.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Controladora Unificada do Sistema de Entregas
 * Gerencia todas as operações de Entregadores, Clientes, Lojas, Entregas, Chat, QR Code e Usuários
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class IdentificadorController {

    private final EntregadorService entregadorService;
    private final ClienteService clienteService;
    private final LojaService lojaService;
    private final EntregaService entregaService;
    private final QRCodeService qrCodeService;
    private final UserService userService;

    // ==================== ENTREGADORES ====================

    @PostMapping("/entregadores/registro")
    public ResponseEntity<Entregador> registrarEntregador(@Valid @RequestBody EntregadorRegistroDTO dto) {
        try {
            log.info("Registrando novo entregador com email: {}", dto.getEmail());
            Entregador entregador = entregadorService.registrarEntregador(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entregador);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao registrar entregador: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/entregadores/{id}")
    public ResponseEntity<Entregador> buscarEntregadorPorId(@PathVariable Long id) {
        try {
            Entregador entregador = entregadorService.buscarPorId(id);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entregadores/email/{email}")
    public ResponseEntity<Entregador> buscarEntregadorPorEmail(@PathVariable String email) {
        try {
            Entregador entregador = entregadorService.buscarPorEmail(email);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entregadores/cpf/{cpf}")
    public ResponseEntity<Entregador> buscarEntregadorPorCpf(@PathVariable String cpf) {
        try {
            Entregador entregador = entregadorService.buscarPorCpf(cpf);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entregadores/qrcode/{qrCodeUuid}")
    public ResponseEntity<Entregador> buscarEntregadorPorQrCode(@PathVariable String qrCodeUuid) {
        try {
            Entregador entregador = entregadorService.buscarPorQrCode(qrCodeUuid);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entregadores")
    public ResponseEntity<List<Entregador>> listarTodosEntregadores() {
        List<Entregador> entregadores = entregadorService.listarTodos();
        return ResponseEntity.ok(entregadores);
    }

    @GetMapping("/entregadores/status/{status}")
    public ResponseEntity<List<Entregador>> buscarEntregadoresPorStatus(@PathVariable StatusEntregador status) {
        List<Entregador> entregadores = entregadorService.buscarPorStatus(status);
        return ResponseEntity.ok(entregadores);
    }

    @GetMapping("/entregadores/disponiveis")
    public ResponseEntity<List<Entregador>> buscarEntregadoresDisponiveis() {
        List<Entregador> entregadores = entregadorService.buscarDisponiveis();
        return ResponseEntity.ok(entregadores);
    }

    @GetMapping("/entregadores/avaliacao/{avaliacaoMinima}")
    public ResponseEntity<List<Entregador>> buscarEntregadoresPorAvaliacaoMinima(@PathVariable Double avaliacaoMinima) {
        List<Entregador> entregadores = entregadorService.buscarPorAvaliacaoMinima(avaliacaoMinima);
        return ResponseEntity.ok(entregadores);
    }

    @PatchMapping("/entregadores/{id}/status")
    public ResponseEntity<Entregador> atualizarStatusEntregador(
            @PathVariable Long id,
            @RequestParam StatusEntregador status) {
        try {
            Entregador entregador = entregadorService.atualizarStatus(id, status);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/entregadores/{id}/aplicativos/adicionar")
    public ResponseEntity<Entregador> adicionarAplicativoEntregador(
            @PathVariable Long id,
            @RequestParam Aplicativo aplicativo) {
        try {
            Entregador entregador = entregadorService.adicionarAplicativo(id, aplicativo);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/entregadores/{id}/aplicativos/remover")
    public ResponseEntity<Entregador> removerAplicativoEntregador(
            @PathVariable Long id,
            @RequestParam Aplicativo aplicativo) {
        try {
            Entregador entregador = entregadorService.removerAplicativo(id, aplicativo);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/entregadores/{id}/aplicativos")
    public ResponseEntity<Entregador> atualizarAplicativosEntregador(
            @PathVariable Long id,
            @RequestBody Set<Aplicativo> aplicativos) {
        try {
            Entregador entregador = entregadorService.atualizarAplicativos(id, aplicativos);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/entregadores/{id}/avaliacao")
    public ResponseEntity<Entregador> atualizarAvaliacaoEntregador(
            @PathVariable Long id,
            @RequestParam Double avaliacao) {
        try {
            if (avaliacao < 0 || avaliacao > 5)
                return ResponseEntity.badRequest().build();

            Entregador entregador = entregadorService.atualizarAvaliacao(id, avaliacao);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/entregadores/{id}")
    public ResponseEntity<Entregador> atualizarDadosEntregador(
            @PathVariable Long id,
            @Valid @RequestBody EntregadorRegistroDTO dto) {
        try {
            Entregador entregador = entregadorService.atualizarDados(id, dto);
            return ResponseEntity.ok(entregador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/entregadores/{id}")
    public ResponseEntity<Void> deletarEntregador(@PathVariable Long id) {
        try {
            entregadorService.deletarEntregador(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/entregadores/{id}/regenerar-qrcode")
    public ResponseEntity<String> regenerarQrCodeEntregador(@PathVariable Long id) {
        try {
            String novoQrCode = entregadorService.regenerarQrCode(id);
            return ResponseEntity.ok(novoQrCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== CLIENTES ====================

    @PostMapping("/clientes/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRegistroDTO dto) {
        try {
            log.info("Registrando novo cliente com email: {}", dto.getEmail());
            Cliente cliente = clienteService.registrarCliente(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensagem", "Cliente registrado com sucesso",
                    "clienteId", cliente.getId(),
                    "nome", cliente.getNome()
            ));
        } catch (Exception e) {
            log.error("Erro ao registrar cliente: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteService.buscarPorId(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clientes/buscar")
    public ResponseEntity<List<Cliente>> buscarClientesPorNome(@RequestParam String nome) {
        log.info("Buscando clientes por nome: {}", nome);
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/clientes/ativos")
    public ResponseEntity<List<Cliente>> buscarClientesAtivos() {
        log.info("Buscando clientes ativos");
        List<Cliente> clientesAtivos = clienteService.buscarClientesAtivos();
        return ResponseEntity.ok(clientesAtivos);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id,
                                              @Valid @RequestBody ClienteRegistroDTO dto) {
        try {
            log.info("Atualizando cliente ID: {}", id);
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, dto);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Cliente atualizado com sucesso",
                    "cliente", clienteAtualizado
            ));
        } catch (Exception e) {
            log.error("Erro ao atualizar cliente: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // ==================== LOJAS ====================

    @PostMapping("/lojas/registro")
    public ResponseEntity<?> registrarLoja(@Valid @RequestBody LojaRegistroDTO dto) {
        try {
            log.info("Registrando nova loja com email: {}", dto.getEmail());
            Loja loja = lojaService.registrarLoja(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensagem", "Loja registrada com sucesso",
                    "lojaId", loja.getId(),
                    "nome", loja.getNome()
            ));
        } catch (Exception e) {
            log.error("Erro ao registrar loja: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/lojas")
    public ResponseEntity<List<Loja>> listarTodasLojas() {
        log.info("Listando todas as lojas");
        List<Loja> lojas = lojaService.listarTodas();
        return ResponseEntity.ok(lojas);
    }

    @GetMapping("/lojas/{id}")
    public ResponseEntity<?> buscarLojaPorId(@PathVariable Long id) {
        log.info("Buscando loja por ID: {}", id);
        return lojaService.buscarPorId(id)
                .map(loja -> ResponseEntity.ok(loja))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lojas/buscar")
    public ResponseEntity<List<Loja>> buscarLojas(@RequestParam(required = false) String nome,
                                                  @RequestParam(required = false) String endereco) {
        if (nome != null && !nome.trim().isEmpty()) {
            log.info("Buscando lojas por nome: {}", nome);
            return ResponseEntity.ok(lojaService.buscarPorNome(nome));
        } else if (endereco != null && !endereco.trim().isEmpty()) {
            log.info("Buscando lojas por endereço: {}", endereco);
            return ResponseEntity.ok(lojaService.buscarPorEndereco(endereco));
        } else
            return ResponseEntity.ok(lojaService.listarTodas());
    }

    @PutMapping("/lojas/{id}")
    public ResponseEntity<?> atualizarLoja(@PathVariable Long id,
                                           @Valid @RequestBody LojaRegistroDTO dto) {
        try {
            log.info("Atualizando loja ID: {}", id);
            Loja lojaAtualizada = lojaService.atualizarLoja(id, dto);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Loja atualizada com sucesso",
                    "loja", lojaAtualizada
            ));
        } catch (Exception e) {
            log.error("Erro ao atualizar loja: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // ==================== ENTREGAS ====================

    @PostMapping("/entregas")
    public ResponseEntity<?> criarEntrega(@Valid @RequestBody CriarEntregaDTO dto) {
        try {
            log.info("Criando nova entrega para loja ID: {}", dto.getLojaId());
            EntregaDTO entrega = entregaService.criarEntrega(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entrega);
        } catch (Exception e) {
            log.error("Erro ao criar entrega: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/entregas/pendentes")
    public ResponseEntity<List<EntregaDTO>> buscarEntregasPendentes() {
        log.info("Buscando entregas pendentes");
        List<EntregaDTO> entregas = entregaService.buscarEntregasPendentes();
        return ResponseEntity.ok(entregas);
    }

    @PostMapping("/entregas/{entregaId}/aceitar/{entregadorId}")
    public ResponseEntity<?> aceitarEntrega(@PathVariable Long entregaId,
                                            @PathVariable Long entregadorId) {
        try {
            log.info("Entregador {} tentando aceitar entrega {}", entregadorId, entregaId);
            EntregaDTO entrega = entregaService.aceitarEntrega(entregaId, entregadorId);
            return ResponseEntity.ok(entrega);
        } catch (Exception e) {
            log.error("Erro ao aceitar entrega: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/entregas/{entregaId}/status")
    public ResponseEntity<?> atualizarStatusEntrega(@PathVariable Long entregaId,
                                                    @Valid @RequestBody AtualizarStatusEntregaDTO dto) {
        try {
            log.info("Atualizando status da entrega {} para: {}", entregaId, dto.getNovoStatus());
            EntregaDTO entrega = entregaService.atualizarStatusEntrega(entregaId, dto);
            return ResponseEntity.ok(entrega);
        } catch (Exception e) {
            log.error("Erro ao atualizar status da entrega: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/entregas/entregador/{entregadorId}")
    public ResponseEntity<List<EntregaDTO>> buscarEntregasDoEntregador(@PathVariable Long entregadorId) {
        log.info("Buscando entregas do entregador: {}", entregadorId);
        List<EntregaDTO> entregas = entregaService.buscarEntregasDoEntregador(entregadorId);
        return ResponseEntity.ok(entregas);
    }

    @GetMapping("/entregas/loja/{lojaId}")
    public ResponseEntity<List<EntregaDTO>> buscarEntregasDaLoja(@PathVariable Long lojaId) {
        log.info("Buscando entregas da loja: {}", lojaId);
        List<EntregaDTO> entregas = entregaService.buscarEntregasDaLoja(lojaId);
        return ResponseEntity.ok(entregas);
    }

    @GetMapping("/entregas/cliente/{clienteId}")
    public ResponseEntity<List<EntregaDTO>> buscarEntregasDoCliente(@PathVariable Long clienteId) {
        log.info("Buscando entregas do cliente: {}", clienteId);
        List<EntregaDTO> entregas = entregaService.buscarEntregasDoCliente(clienteId);
        return ResponseEntity.ok(entregas);
    }

    // ==================== QR CODE ====================

    @GetMapping("/qrcode/generate")
    public ResponseEntity<byte[]> generateSimpleQRCode(@RequestParam String text) {
        try {
            log.info("Gerando QR Code simples para texto: {}", text);
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);

        } catch (Exception e) {
            log.error("Erro ao gerar QR Code simples: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar QR Code: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/qrcode/custom")
    public ResponseEntity<byte[]> generateCustomQRCode(@RequestBody QRCode qrCodeRequest) {
        try {
            if (qrCodeRequest.getText() == null || qrCodeRequest.getText().trim().isEmpty()) {
                log.warn("Tentativa de gerar QR Code com texto vazio");
                return ResponseEntity.badRequest()
                        .body("Texto não pode estar vazio".getBytes());
            }

            log.info("Gerando QR Code customizado - Dimensões: {}x{}",
                    qrCodeRequest.getWidth(), qrCodeRequest.getHeight());

            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);

        } catch (Exception e) {
            log.error("Erro ao gerar QR Code customizado: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar QR Code: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/qrcode/test")
    public ResponseEntity<String> testQRCodeEndpoint() {
        log.info("Endpoint de teste acessado");
        return ResponseEntity.ok("QR Code Service está funcionando!");
    }

    // ==================== SYSTEM TEST ====================

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Sistema de Entregas funcionando corretamente",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    // ==================== USUÁRIOS ====================

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        log.info("GET /api/users - Listando todos os usuários");

        List<Map<String, Object>> users = userService.findAll().stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());

        log.info("Retornando {} usuários", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Buscando usuário", id);

        return userService.findById(id)
                .map(user -> {
                    log.info("Usuário encontrado: {}", user.getEmail());
                    return ResponseEntity.ok(convertUserToResponse(user));
                })
                .orElseGet(() -> {
                    log.warn("Usuário não encontrado com ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{} - Buscando usuário por email", email);

        return userService.findByEmail(email)
                .map(user -> {
                    log.info("Usuário encontrado: {}", user.getNome());
                    return ResponseEntity.ok(convertUserToResponse(user));
                })
                .orElseGet(() -> {
                    log.warn("Usuário não encontrado com email: {}", email);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsersByName(@RequestParam String nome) {
        log.info("GET /api/users/search?nome={} - Buscando usuários", nome);

        List<Map<String, Object>> users = userService.findByNomeContaining(nome).stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());

        log.info("Encontrados {} usuários", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/created-after")
    public ResponseEntity<List<Map<String, Object>>> getUsersCreatedAfter(@RequestParam String date) {
        log.info("GET /api/users/created-after?date={}", date);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Map<String, Object>> users = userService.findUsersCreatedAfter(dateTime).stream()
                    .map(this::convertUserToResponse)
                    .collect(Collectors.toList());

            log.info("Encontrados {} usuários criados após {}", users.size(), date);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Erro ao parsear data: {}", date, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationDTO dto) {
        log.info("PUT /api/users/{} - Atualizando usuário", id);

        try {
            User updatedUser = userService.update(id, dto);
            log.info("Usuário atualizado com sucesso: {}", updatedUser.getEmail());
            return ResponseEntity.ok(convertUserToResponse(updatedUser));
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deletando usuário", id);

        try {
            userService.delete(id);
            log.info("Usuário deletado com sucesso");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário deletado com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erro ao deletar usuário ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/validate-login")
    public ResponseEntity<Map<String, Object>> validateLogin(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("POST /api/users/validate-login - Validando login para: {}", loginDTO.getEmail());

        return userService.validateLogin(loginDTO)
                .map(user -> {
                    log.info("Login validado com sucesso para: {}", loginDTO.getEmail());
                    Map<String, Object> response = convertUserToResponse(user);
                    response.put("authenticated", true);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Login inválido para: {}", loginDTO.getEmail());
                    Map<String, Object> response = new HashMap<>();
                    response.put("authenticated", false);
                    response.put("message", "Email ou senha inválidos");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                });
    }

    @GetMapping("/users/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable String email) {
        log.info("GET /api/users/check-email/{} - Verificando se email existe", email);

        boolean exists = userService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        log.info("Email {} existe: {}", email, exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        log.info("GET /api/users/count - Contando usuários");

        long count = userService.count();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        log.info("Total de usuários: {}", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/emails")
    public ResponseEntity<List<String>> getAllEmails() {
        log.info("GET /api/users/emails - Listando todos os emails");

        List<String> emails = userService.getAllEmails();
        log.info("Retornando {} emails", emails.size());
        return ResponseEntity.ok(emails);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Map<String, Object> convertUserToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("nome", user.getNome());
        response.put("email", user.getEmail());
        response.put("telefone", user.getTelefone());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        return response;
    }

    @MessageMapping("/chat.enviarMensagem")
    @SendTo("/topico/publico")
    public MensagemChat enviarMensagem(@Payload MensagemChat mensagem) {
        log.info("Mensagem recebida de {}: {}", mensagem.getRemetente(), mensagem.getConteudo());
        return mensagem;
    }

    @MessageMapping("/chat.adicionarUsuario")
    @SendTo("/topico/publico")
    public MensagemChat adicionarUsuario(@Payload MensagemChat mensagem,
                                         SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("nomeUsuario", mensagem.getRemetente());
        log.info("Usuário conectado: {}", mensagem.getRemetente());
        return mensagem;
    }

    @MessageMapping("/chat.mensagemPrivada")
    @SendTo("/topico/privado")
    public MensagemChat enviarMensagemPrivada(@Payload MensagemChat mensagem) {
        log.info("Mensagem privada de {} para {}: {}",
                mensagem.getRemetente(), mensagem.getDestinatarioId(), mensagem.getConteudo());
        return mensagem;
    }
}