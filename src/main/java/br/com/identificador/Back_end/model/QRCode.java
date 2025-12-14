package br.com.identificador.Back_end.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto para configuração de geração de QR Code personalizado")
public class QRCode {

    @Schema(description = "Texto a ser codificado no QR Code", example = "https://identificador.com.br/entregador/550e8400-e29b-41d4-a716-446655440000", required = true)
    private String text;

    @Schema(description = "Largura da imagem do QR Code em pixels", example = "300", defaultValue = "300", minimum = "100", maximum = "1000")
    private int width = 300;

    @Schema(description = "Altura da imagem do QR Code em pixels", example = "300", defaultValue = "300", minimum = "100", maximum = "1000")
    private int height = 300;

    @Schema(description = "Formato da imagem gerada", example = "PNG", defaultValue = "PNG", allowableValues = {"PNG", "JPG", "JPEG"})
    private String format = "PNG";

    public QRCode(String text) {
        this.text = text;
        this.width = 300;
        this.height = 300;
        this.format = "PNG";
    }
}