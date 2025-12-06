package br.com.identificador.Back_end.model.enuns;

public enum Aplicativo {
    IFOOD("iFood"),
    UBER_EATS("Uber Eats"),
    RAPPI("Rappi"),
    LOGGI("Loggi"),
    JAMES_DELIVERY("James Delivery"),
    NINETY_NINE_FOOD("99 Food"),
    AIQFOME("aiqfome"),
    OUTROS("Outros");

    private final String nomeExibicao;

    Aplicativo(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
