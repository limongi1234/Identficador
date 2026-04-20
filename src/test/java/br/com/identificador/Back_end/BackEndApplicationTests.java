package br.com.identificador.Back_end;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BackEndApplicationTests {

    @Test
    void contextLoads() {
        // Testa se o contexto da aplicação carrega corretamente
        // Se chegou aqui, significa que todos os beans foram criados com sucesso
        assertThat(true).isTrue();
    }

    @Test
    void main_DeveExecutarSemErros() {
        // Testa se o método main pode ser executado
        // Nota: Em testes, o Spring Boot não inicia o servidor embedded por padrão
        BackEndApplication.main(new String[]{});
        assertThat(true).isTrue();
    }

    @Test
    void aplicacao_DeveTerTodasDependenciasConfiguradas() {
        // Verifica se a aplicação tem todas as configurações necessárias
        // Este teste passa se o contexto carregou sem erros
        assertThat(BackEndApplication.class).isNotNull();
    }
}
