package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.model.QRCode;
import br.com.identificador.Back_end.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IdentificadorController {

    @Autowired
    private QRCodeService qrCodeService;

    /**
     * Gera QR Code simples via parâmetro
     * GET /api/qrcode/generate?text=Hello World
     */
    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> generateSimpleQRCode(@RequestParam String text) {
        try {
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar QR Code: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Gera QR Code com configurações personalizadas via POST
     * POST /api/qrcode/custom
     * Body: {"text": "Hello", "width": 400, "height": 400}
     */
    @PostMapping("/custom")
    public ResponseEntity<byte[]> generateCustomQRCode(@RequestBody QRCode qrCodeRequest) {
        try {
            // Validação básica
            if (qrCodeRequest.getText() == null || qrCodeRequest.getText().trim().isEmpty())
                return ResponseEntity.badRequest()
                        .body("Texto não pode estar vazio".getBytes());

            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar QR Code: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Endpoint de teste para verificar se o serviço está funcionando
     */
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("QR Code Service está funcionando!");
    }
}
