package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento completo de usuários")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listarTodos() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Erro ao listar usuários: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<User> buscarPorId(@PathVariable @Parameter(description = "ID do usuário") Long id) {
        try {
            return userService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar usuário com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuário por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<User> buscarPorEmail(@PathVariable @Parameter(description = "Email do usuário") String email) {
        try {
            return userService.findByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar usuário com email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar usuários por nome (parcial)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<User>> buscarPorNome(@PathVariable @Parameter(description = "Parte do nome") String nome) {
        try {
            List<User> users = userService.findByNomeContaining(nome);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Erro ao buscar usuários por nome '{}': {}", nome, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/criados-apos")
    @Operation(summary = "Usuários criados após data específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<User>> buscarCriadosApos(
            @RequestParam @Parameter(description = "Data mínima (ISO format)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        try {
            List<User> users = userService.findUsersCreatedAfter(data);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Erro ao buscar usuários criados após {}: {}", data, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar total de usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total de usuários"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> contarUsuarios() {
        try {
            long count = userService.count();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erro ao contar usuários: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/emails")
    @Operation(summary = "Listar todos os emails")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de emails"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> listarEmails() {
        try {
            List<String> emails = userService.getAllEmails();
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Erro ao listar emails: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Validar login", description = "Autentica usuário e retorna dados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login válido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<User> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            return userService.validateLogin(loginDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        } catch (AuthenticationException e) {
            log.warn("Falha de autenticação para email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Erro no login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Email duplicado ou dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> atualizarUsuario(
            @PathVariable @Parameter(description = "ID do usuário") Long id,
            @Valid @RequestBody UserRegistrationDTO dto) {
        try {
            User updatedUser = userService.update(id, dto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found"))
                return ResponseEntity.notFound().build();
            else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate"))
                return ResponseEntity.badRequest().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário deletado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable @Parameter(description = "ID do usuário") Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar usuário ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found"))
                return ResponseEntity.notFound().build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
