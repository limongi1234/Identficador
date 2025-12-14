package br.com.identificador.Back_end.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Health Check", description = "Endpoint para verificação de saúde do sistema")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Verificar saúde do sistema",
            description = """
                Endpoint de health check para monitoramento do sistema.
                
                Retorna status do serviço e timestamp atual.
                
                **Uso comum:**
                - Monitoramento com ferramentas como Prometheus, Grafana
                - Load balancers (Kubernetes, Docker Swarm)
                - Scripts de deploy CI/CD
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sistema funcionando normalmente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Sistema UP",
                                    value = """
                                    {
                                      "status": "UP",
                                      "message": "Sistema de Entregas funcionando corretamente",
                                      "timestamp": "2025-12-14T22:19:30.123"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Sistema com problemas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Sistema DOWN",
                                    value = """
                                    {
                                      "status": "DOWN",
                                      "message": "Erro interno no servidor",
                                      "timestamp": "2025-12-14T22:19:30.123"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, String>> healthCheck() {
        log.info("Health check executado");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Sistema de Entregas funcionando corretamente",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
