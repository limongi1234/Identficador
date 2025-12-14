package br.com.identificador.Back_end.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI para o Sistema de Identificação de Entregadores
 * 
 * Esta classe configura a documentação automática da API REST, incluindo:
 * - Informações gerais da API
 * - Servidores disponíveis
 * - Autenticação JWT
 * - Agrupamento de endpoints por tags
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema de Identificação de Entregadores")
                        .version("1.0.0")
                        .description("""
                                API REST completa para gerenciamento de entregas, entregadores, clientes e lojas.
                                
                                ## Funcionalidades principais:
                                - Autenticação JWT com múltiplos perfis de usuário
                                - Gerenciamento completo de entregadores (CPF, RG, CNH, QR Code)
                                - Controle de status e disponibilidade de entregadores
                                - Sistema de entregas com rastreamento
                                - Geração de QR Codes personalizados
                                - Chat em tempo real (WebSocket)
                                - Avaliações e estatísticas
                                
                                ## Tipos de usuário:
                                - **Entregador**: Profissional que realiza entregas
                                - **Cliente**: Pessoa que solicita entregas
                                - **Loja**: Estabelecimento comercial que envia produtos
                                
                                ## Autenticação:
                                Para acessar endpoints protegidos, faça login em `/api/auth/login` e use o token JWT retornado.
                                """)
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento - Sistema Identificador")
                                .email("contato@identificador.com.br")
                                .url("https://identificador.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento Local"),
                        new Server()
                                .url("https://api-homolog.identificador.com.br")
                                .description("Servidor de Homologação"),
                        new Server()
                                .url("https://api.identificador.com.br")
                                .description("Servidor de Produção")
                ))
                
                // Configuração de segurança JWT
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Token JWT obtido no endpoint /api/auth/login. Formato: Bearer {token}")))
                
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                
                // Tags para organização dos endpoints
                .tags(List.of(
                        new Tag()
                                .name("Autenticação")
                                .description("Endpoints de login, validação de token e informações do usuário atual"),
                        new Tag()
                                .name("Entregadores")
                                .description("CRUD completo de entregadores, gerenciamento de status, aplicativos, avaliações e QR Codes"),
                        new Tag()
                                .name("Clientes")
                                .description("Gerenciamento de clientes e suas entregas"),
                        new Tag()
                                .name("Lojas")
                                .description("Cadastro e gerenciamento de lojas parceiras"),
                        new Tag()
                                .name("Entregas")
                                .description("Sistema completo de entregas: criação, aceitação, atualização de status e rastreamento"),
                        new Tag()
                                .name("QR Code")
                                .description("Geração de QR Codes simples e customizados para identificação de entregadores"),
                        new Tag()
                                .name("Usuários")
                                .description("Gerenciamento genérico de usuários do sistema"),
                        new Tag()
                                .name("Chat")
                                .description("Sistema de mensagens em tempo real via WebSocket"),
                        new Tag()
                                .name("Sistema")
                                .description("Endpoints de monitoramento e verificação de saúde do sistema")
                ));
    }
}