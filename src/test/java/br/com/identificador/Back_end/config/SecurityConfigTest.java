package br.com.identificador.Back_end.config;

import br.com.identificador.Back_end.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void contexto_DeveCarregarComSucesso() {
        // Verifica se o contexto Spring carrega corretamente com a configuração de segurança
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void endpointPublico_DeveSerAcessivelSemAutenticacao() throws Exception {
        // Testa endpoint público
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void endpointProtegido_DeveExigirAutenticacao() throws Exception {
        // Testa endpoint protegido
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swagger_DeveSerAcessivel() throws Exception {
        // Testa acesso ao Swagger
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void h2Console_DeveSerAcessivel() throws Exception {
        // Testa acesso ao H2 Console (se habilitado)
        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isOk());
    }
}
