package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.model.QRCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class QRCodeService {

    /**
     * Gera QR Code como array de bytes
     */
    public byte[] generateQRCodeImage(QRCode qrCode) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                qrCode.getText(),
                BarcodeFormat.QR_CODE,
                qrCode.getWidth(),
                qrCode.getHeight()
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, qrCode.getFormat(), outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Gera QR Code como array de bytes - método simplificado
     */
    public byte[] generateQRCodeImage(String text) throws WriterException, IOException {
        return generateQRCodeImage(new QRCode(text));
    }

    /**
     * Salva QR Code em arquivo
     */
    public void saveQRCodeToFile(QRCode qrCode, Path filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                qrCode.getText(),
                BarcodeFormat.QR_CODE,
                qrCode.getWidth(),
                qrCode.getHeight()
        );

        MatrixToImageWriter.writeToPath(bitMatrix, qrCode.getFormat(), filePath);
    }
}