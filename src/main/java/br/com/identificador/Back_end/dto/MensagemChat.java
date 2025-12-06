package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.TipoMensagem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagemChat {
    private TipoMensagem tipoMensagem;
    private String conteudo;
    private String remetente;
    private String horaEnvio;
    private Long destinatarioId;
    private String sala;
}
