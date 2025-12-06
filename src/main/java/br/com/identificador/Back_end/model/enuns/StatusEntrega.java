package br.com.identificador.Back_end.model.enuns;

public enum StatusEntrega {
    PENDENTE("Pendente"),
    ACEITA("Aceita"),
    EM_ANDAMENTO("Em Andamento"),
    COLETADA("Coletada"),
    A_CAMINHO("A Caminho"),
    ENTREGUE("Entregue"),
    CANCELADA("Cancelada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    StatusEntrega(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
