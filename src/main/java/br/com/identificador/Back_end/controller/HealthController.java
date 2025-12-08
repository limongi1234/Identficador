package br.com.identificador.Back_end.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        log.info("Health check executado");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Sistema de Entregas funcionando corretamente",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}