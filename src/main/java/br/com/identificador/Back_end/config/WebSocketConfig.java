package br.com.identificador.Back_end.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("Configurando broker de mensagens WebSocket");
        
        // Habilitar broker simples para tópicos
        config.enableSimpleBroker("/topico", "/fila");
        
        // Prefixo para destinos da aplicação
        config.setApplicationDestinationPrefixes("/app");
        
        // Configurar destino para usuários específicos
        config.setUserDestinationPrefix("/usuario");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registrando endpoints STOMP");
        
        // Endpoint principal com SockJS como fallback
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
        
        // Endpoint para chat privado
        registry.addEndpoint("/ws-privado")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}

