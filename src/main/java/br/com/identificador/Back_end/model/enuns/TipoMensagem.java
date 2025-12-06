package br.com.identificador.Back_end.model.enuns;

public enum TipoMensagem {
    CHAT("Chat"),
    ENTRADA("Entrada"),
    SAIDA("Saída"),
    SISTEMA("Sistema");

    private final String descricao;

    TipoMensagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
