package br.com.identificador.Back_end.dto;

import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {
    private Long id;
    private String nome;
    private String telefone;
    private StatusEntregador status;
    private Set<Aplicativo> aplicativos;
    private Double avaliacaoMedia;
    private Integer totalEntregas;
    private String qrCodeUuid;
}

