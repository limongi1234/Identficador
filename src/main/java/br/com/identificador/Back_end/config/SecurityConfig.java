package br.com.identificador.Back_end.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Configurando encoder de senha BCrypt");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configurando cadeia de filtros de segurança");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        // Endpoints de autenticação - públicos
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoints de registro - públicos
                        .requestMatchers("/api/entregadores/registro").permitAll()
                        .requestMatchers("/api/lojas/registro").permitAll()
                        .requestMatchers("/api/clientes/registro").permitAll()

                        // Perfis públicos e QR codes
                        .requestMatchers("/api/entregadores/perfil/**").permitAll()
                        .requestMatchers("/api/entregadores/qrcode/**").permitAll()

                        // WebSocket endpoints
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws-privado/**").permitAll()

                        // Console H2 para desenvolvimento
                        .requestMatchers("/h2-console/**").permitAll()

                        // Endpoints de saúde da aplicação
                        .requestMatchers("/actuator/health").permitAll()

                        // Swagger UI (se habilitado)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        log.info("Configuração de segurança concluída");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Configurando CORS");

        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir origens específicas em produção
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir credenciais
        configuration.setAllowCredentials(true);

        // Headers expostos
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type"
        ));

        // Tempo de cache para preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("Configuração CORS concluída");
        return source;
    }
}