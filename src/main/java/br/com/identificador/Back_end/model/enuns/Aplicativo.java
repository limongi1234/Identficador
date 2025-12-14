package br.com.identificador.Back_end.model.enuns;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aplicativos de delivery que o entregador pode utilizar",
        allowableValues = {"IFOOD", "RAPPI", "UBER_EATS", "FOOD_99",
                "LOGGI", "JAMES", "ZE_DELIVERY", "INDEPENDENTE", "OUTROS"})
public enum Aplicativo {

    @Schema(description = "iFood - Aplicativo de delivery de comida")
    IFOOD("iFood"),

    @Schema(description = "Rappi - Aplicativo de entregas diversas")
    RAPPI("Rappi"),

    @Schema(description = "Uber Eats - Aplicativo de delivery de comida da Uber")
    UBER_EATS("Uber Eats"),

    @Schema(description = "99Food - Aplicativo de delivery de comida da 99")
    FOOD_99("99Food"),

    @Schema(description = "Loggi - Aplicativo de entregas e logística")
    LOGGI("Loggi"),

    @Schema(description = "James Delivery - Aplicativo de entregas")
    JAMES("James Delivery"),

    @Schema(description = "Zé Delivery - Aplicativo especializado em bebidas")
    ZE_DELIVERY("Zé Delivery"),

    @Schema(description = "Aplicativo independente/próprio")
    INDEPENDENTE("Independente"),

    @Schema(description = "Outros aplicativos não listados")
    OUTROS("Outros");

    private final String nomeExibicao;

    Aplicativo(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
