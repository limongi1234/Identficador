package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.model.QRCode;
import br.com.identificador.Back_end.service.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcodes")
@RequiredArgsConstructor
@Tag(name = "QR Codes", description = "Geração de QR Codes")
@SecurityRequirement(name = "bearerAuth")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @PostMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Gerar QR Code", description = "Gera imagem QR Code a partir do texto fornecido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR Code gerado com sucesso", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "image/png")),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<byte[]> generateQRCode(
            @Valid @RequestBody QRCode qrCodeRequest) {
        
        try {
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeRequest);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            headers.set("Content-Disposition", "inline; filename=qrcode.png");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/generate/text", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Gerar QR Code simples", description = "Gera QR Code apenas com texto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR Code gerado", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "image/png")),
        @ApiResponse(responseCode = "400", description = "Texto inválido")
    })
    public ResponseEntity<byte[]> generateQRCodeFromText(
            @RequestParam @Parameter(description = "Texto para QR Code") String text) {
        
        try {
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(text);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            headers.set("Content-Disposition", "inline; filename=qrcode.png");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/{text}")
    @Operation(summary = "Preview QR Code", description = "Gera QR Code para visualização direta no browser")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR Code exibido", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "image/png"))
    })
    public ResponseEntity<byte[]> previewQRCode(
            @PathVariable @Parameter(description = "Texto para QR Code") String text) {
        
        try {
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(text);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header("Content-Disposition", "inline; filename=qrcode-" + text.hashCode() + ".png")
                    .body(qrCodeImage);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
