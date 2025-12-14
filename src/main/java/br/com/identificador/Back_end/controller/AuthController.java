package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.service.CustomUserDetailsService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints para autenticação JWT e gerenciamento de sessão")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    @Operation(
            summary = "Realizar login no sistema",
            description = """
                Autentica um usuário (Entregador, Cliente ou Loja) e retorna um token JWT válido por 10 horas.
                
                O token deve ser incluído no header Authorization de todas as requisições protegidas:
                ```
                Authorization: Bearer {seu-token-jwt}
                ```
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de login",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Login Entregador",
                                            value = "{\"email\": \"entregador@email.com\", \"senha\": \"senha123\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Login Cliente",
                                            value = "{\"email\": \"cliente@email.com\", \"senha\": \"senha123\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Login Loja",
                                            value = "{\"email\": \"loja@email.com\", \"senha\": \"senha123\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso. Retorna token JWT e informações do usuário",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "type": "Bearer",
                      "expiresIn": 36000,
                      "user": {
                        "id": 1,
                        "nome": "João Silva",
                        "email": "joao@email.com",
                        "telefone": "(21) 98765-4321",
                        "userType": "Entregador",
                        "role": "ROLE_ENTREGADOR"
                      }
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "error": "Credenciais inválidas",
                      "message": "Email ou senha incorretos"
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor"
            )
    })
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            log.info("Tentativa de login para: {}", loginDTO.getEmail());

            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getSenha()
                    )
            );

            // Busca dados completos do usuário
            User user = userDetailsService.findCompleteUserByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Determina o tipo de usuário
            String userType = user.getClass().getSimpleName();
            String role = determineRole(user);

            // Gera o token JWT
            Instant now = Instant.now();
            long expiry = 36000L; // 10 horas

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("identificador-api")
                    .issuedAt(now)
                    .expiresAt(now.plus(expiry, ChronoUnit.SECONDS))
                    .subject(authentication.getName())
                    .claim("userId", user.getId())
                    .claim("email", user.getEmail())
                    .claim("nome", user.getNome())
                    .claim("userType", userType)
                    .claim("role", role)
                    .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            // Prepara resposta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("expiresIn", expiry);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "nome", user.getNome(),
                    "email", user.getEmail(),
                    "telefone", user.getTelefone(),
                    "userType", userType,
                    "role", role
            ));

            log.info("Login bem-sucedido para: {} (Tipo: {})", loginDTO.getEmail(), userType);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            log.error("Falha na autenticação para: {}", loginDTO.getEmail());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciais inválidas");
            errorResponse.put("message", "Email ou senha incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Erro no login: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro interno");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/validate")
    @Operation(
            summary = "Validar token JWT",
            description = "Verifica se o token JWT fornecido é válido e retorna informações sobre o token",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token válido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "valid": true,
                      "email": "joao@email.com",
                      "authorities": ["ROLE_ENTREGADOR"]
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido ou expirado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "valid": false,
                      "message": "Token inválido ou expirado"
                    }
                    """)
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(hidden = true) Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("email", authentication.getName());
            response.put("authorities", authentication.getAuthorities());

            log.info("Token validado para: {}", authentication.getName());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("valid", false);
        response.put("message", "Token inválido ou expirado");

        log.warn("Tentativa de validação de token inválido");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obter dados do usuário autenticado",
            description = "Retorna as informações completas do usuário atualmente autenticado baseado no token JWT",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações do usuário retornadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "nome": "João Silva",
                      "email": "joao@email.com",
                      "telefone": "(21) 98765-4321",
                      "userType": "Entregador",
                      "role": "ROLE_ENTREGADOR"
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao buscar informações do usuário"
            )
    })
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @Parameter(hidden = true) Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = authentication.getName();
            User user = userDetailsService.findCompleteUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("nome", user.getNome());
            response.put("email", user.getEmail());
            response.put("telefone", user.getTelefone());
            response.put("userType", user.getClass().getSimpleName());
            response.put("role", determineRole(user));

            log.info("Informações do usuário atual retornadas: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao buscar usuário atual: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineRole(User user) {
        String className = user.getClass().getSimpleName();
        return "ROLE_" + className.toUpperCase();
    }
}