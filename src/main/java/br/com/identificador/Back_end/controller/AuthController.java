package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.service.CustomUserDetailsService;
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
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
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
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
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
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
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