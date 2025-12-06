package br.com.identificador.Back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    private String text;
    private int width = 300;
    private int height = 300;
    private String format = "PNG";
    
    public QRCode(String text) {
        this.text = text;
        this.width = 300;
        this.height = 300;
        this.format = "PNG";
    }
}