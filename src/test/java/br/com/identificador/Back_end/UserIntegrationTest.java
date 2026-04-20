package br.com.identificador.Back_end;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserRegistrationDTO registrationDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste
        userRepository.deleteAll();

        registrationDTO = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        loginDTO = new LoginDTO("joao@email.com", "senha123");
    }

    @Test
    void fluxoCompletoUsuario_DeveFuncionarDoInicioAoFim() throws Exception {
        // 1. Registrar usuário
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        // 2. Fazer login
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        // 3. Buscar usuário por email
        mockMvc.perform(get("/api/users/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));

        // 4. Buscar usuário por nome
        mockMvc.perform(get("/api/users/nome/João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        // 5. Contar usuários (como admin)
        mockMvc.perform(get("/api/users/count")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        // 6. Listar emails (como admin)
        mockMvc.perform(get("/api/users/emails")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("joao@email.com"));
    }

    @Test
    void registro_DeveValidarEmailDuplicado() throws Exception {
        // Primeiro registro
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Segundo registro com mesmo email
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_DeveFalharComCredenciaisInvalidas() throws Exception {
        // Registrar usuário
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Tentar login com senha errada
        LoginDTO wrongPassword = new LoginDTO("joao@email.com", "senhaerrada");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPassword))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        // Tentar login com email inexistente
        LoginDTO wrongEmail = new LoginDTO("inexistente@email.com", "senha123");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongEmail))
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void atualizarUsuario_DeveFuncionarComDadosValidos() throws Exception {
        // Registrar usuário
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Buscar ID do usuário criado
        User createdUser = userRepository.findByEmail("joao@email.com").orElseThrow();

        // Atualizar dados
        UserRegistrationDTO updateDTO = new UserRegistrationDTO(
            "João Silva Atualizado",
            "joao@email.com",
            "11888888888",
            "novaSenha123"
        );

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.telefone").value("11888888888"));
    }

    @Test
    void deletarUsuario_DeveRemoverUsuarioDoSistema() throws Exception {
        // Registrar usuário
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Buscar ID do usuário criado
        User createdUser = userRepository.findByEmail("joao@email.com").orElseThrow();

        // Deletar usuário
        mockMvc.perform(delete("/api/users/" + createdUser.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verificar que não existe mais
        mockMvc.perform(get("/api/users/email/joao@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void endpointsProtegidos_DeveExigirAutenticacao() throws Exception {
        // Tentar acessar endpoints protegidos sem autenticação
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/emails"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validacao_DeveRejeitarDadosInvalidos() throws Exception {
        // DTO com dados inválidos
        UserRegistrationDTO invalidDTO = new UserRegistrationDTO(
            "", // nome vazio
            "email-invalido", // email inválido
            "", // telefone vazio
            "" // senha vazia
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
