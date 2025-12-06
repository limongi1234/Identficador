package br.com.identificador.Back_end.exceptions;

import com.google.zxing.WriterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class QRCodeExceptionHandler {

    @ExceptionHandler(WriterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleWriterException(WriterException e) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Erro ao gerar QR Code");
        error.put("detalhes", e.getMessage());
        error.put("codigo", "QRCODE_GENERATION_ERROR");
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Erro de I/O ao processar QR Code");
        error.put("detalhes", e.getMessage());
        error.put("codigo", "IO_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Parâmetros inválidos");
        error.put("detalhes", e.getMessage());
        error.put("codigo", "INVALID_PARAMETERS");
        
        return ResponseEntity.badRequest().body(error);
    }
}