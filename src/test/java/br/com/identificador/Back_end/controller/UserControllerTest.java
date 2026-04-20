package br.com.identificador.Back_end.controller;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserRegistrationDTO registrationDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNome("João Silva");
        testUser.setEmail("joao@email.com");
        testUser.setTelefone("11999999999");

        registrationDTO = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        loginDTO = new LoginDTO("joao@email.com", "senha123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodos_DeveRetornarListaDeUsuarios() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(userService).findAll();
    }

    @Test
    void buscarPorId_DeveRetornarUsuarioQuandoEncontrado() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(userService).findById(1L);
    }

    @Test
    void buscarPorId_DeveRetornar404QuandoNaoEncontrado() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).findById(1L);
    }

    @Test
    void buscarPorEmail_DeveRetornarUsuarioQuandoEncontrado() throws Exception {
        when(userService.findByEmail("joao@email.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(userService).findByEmail("joao@email.com");
    }

    @Test
    void buscarPorNome_DeveRetornarListaDeUsuarios() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findByNomeContaining("João")).thenReturn(users);

        mockMvc.perform(get("/api/users/nome/João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(userService).findByNomeContaining("João");
    }

    @Test
    void login_DeveRetornarUsuarioQuandoCredenciaisValidas() throws Exception {
        when(userService.validateLogin(loginDTO)).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(userService).validateLogin(loginDTO);
    }

    @Test
    void login_DeveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        when(userService.validateLogin(loginDTO)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService).validateLogin(loginDTO);
    }

    @Test
    @WithMockUser(roles = "USER")
    void atualizarUsuario_DeveRetornarUsuarioAtualizado() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setNome("João Silva Atualizado");
        updatedUser.setEmail("joao@email.com");

        when(userService.update(eq(1L), any(UserRegistrationDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));

        verify(userService).update(eq(1L), any(UserRegistrationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarUsuario_DeveRetornar204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void contarUsuarios_DeveRetornarContagem() throws Exception {
        when(userService.count()).thenReturn(5L);

        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(userService).count();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarEmails_DeveRetornarListaDeEmails() throws Exception {
        List<String> emails = Arrays.asList("joao@email.com", "maria@email.com");
        when(userService.getAllEmails()).thenReturn(emails);

        mockMvc.perform(get("/api/users/emails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("joao@email.com"))
                .andExpect(jsonPath("$[1]").value("maria@email.com"));

        verify(userService).getAllEmails();
    }

    @Test
    void login_DeveValidarCamposObrigatorios() throws Exception {
        LoginDTO invalidLogin = new LoginDTO("", "");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).validateLogin(any());
    }

    @Test
    void atualizarUsuario_DeveValidarCamposObrigatorios() throws Exception {
        UserRegistrationDTO invalidDTO = new UserRegistrationDTO("", "invalid-email", "", "");

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(anyLong(), any());
    }
}
