package br.com.identificador.Back_end;

import br.com.identificador.Back_end.dto.*;
import br.com.identificador.Back_end.model.*;
import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntrega;
import br.com.identificador.Back_end.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class FullSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private EntregaRepository entregaRepository;

    private ClienteRegistroDTO clienteDTO;
    private EntregadorRegistroDTO entregadorDTO;
    private LojaRegistroDTO lojaDTO;
    private CriarEntregaDTO entregaDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste
        entregaRepository.deleteAll();
        clienteRepository.deleteAll();
        entregadorRepository.deleteAll();
        lojaRepository.deleteAll();
        userRepository.deleteAll();

        clienteDTO = new ClienteRegistroDTO(
            "João Cliente",
            "cliente@email.com",
            "11999999999",
            "senha123",
            "12345678901",
            "Rua dos Clientes, 123"
        );

        entregadorDTO = new EntregadorRegistroDTO(
            "Maria Entregadora",
            "entregador@email.com",
            "11888888888",
            "senha123",
            "98765432100",
            "987654321",
            "98765432100",
            Set.of(Aplicativo.IFOOD, Aplicativo.RAPPI)
        );

        lojaDTO = new LojaRegistroDTO(
            "Loja Exemplo",
            "loja@email.com",
            "11777777777",
            "senha123",
            "12345678000123",
            "Rua das Lojas, 456",
            "Responsável da Loja",
            "Seg-Sex: 8h-18h"
        );

        entregaDTO = new CriarEntregaDTO(
            1L, // lojaId
            1L, // clienteId
            "Rua de Origem, 123",
            "Rua de Destino, 456",
            "Produto de teste",
            25.50, // valorEntrega
            5.00   // valorGorjeta
        );

        loginDTO = new LoginDTO("cliente@email.com", "senha123");
    }

    @Test
    void fluxoCompletoSistema_DeveFuncionarDoInicioAoFim() throws Exception {
        // 1. Registrar cliente
        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Cliente"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        // 2. Registrar entregador
        mockMvc.perform(post("/api/entregadores/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entregadorDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Entregadora"))
                .andExpect(jsonPath("$.cpf").value("98765432100"));

        // 3. Registrar loja
        mockMvc.perform(post("/api/lojas/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lojaDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Loja Exemplo"))
                .andExpect(jsonPath("$.cnpj").value("12345678000123"));

        // 4. Fazer login como cliente
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("cliente@email.com"));

        // 5. Criar entrega
        mockMvc.perform(post("/api/entregas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entregaDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoOrigem").value("Rua de Origem, 123"))
                .andExpect(jsonPath("$.enderecoDestino").value("Rua de Destino, 456"))
                .andExpect(jsonPath("$.statusEntrega").value("A_CAMINHO_COLETA"));

        // 6. Listar entregas
        mockMvc.perform(get("/api/entregas")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].produtoDescricao").value("Produto de teste"));

        // 7. Buscar entregadores disponíveis
        mockMvc.perform(get("/api/entregadores/disponiveis")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Maria Entregadora"));

        // 8. Obter perfil público do entregador
        mockMvc.perform(get("/api/entregadores/perfil/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Entregadora"));

        // 9. Gerar QR code do entregador
        mockMvc.perform(get("/api/qrcode/entregador/1")
                .with(csrf()))
                .andExpect(status().isOk());

        // 10. Verificar health check
        mockMvc.perform(get("/api/health")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void validacoesNegocio_DeveImpedirOperacoesInvalidas() throws Exception {
        // Tentar registrar cliente com email duplicado
        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        // Tentar registrar entregador com CPF duplicado
        mockMvc.perform(post("/api/entregadores/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entregadorDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        EntregadorRegistroDTO entregadorDuplicado = new EntregadorRegistroDTO(
            "Outro Entregador",
            "outro@email.com",
            "11666666666",
            "senha123",
            "98765432100", // Mesmo CPF
            "111111111",
            "11111111100",
            Set.of(Aplicativo.UBER_EATS)
        );

        mockMvc.perform(post("/api/entregadores/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entregadorDuplicado))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizacoes_DevePermitirModificacoesValidas() throws Exception {
        // Registrar cliente
        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Atualizar dados do cliente
        ClienteRegistroDTO updateCliente = new ClienteRegistroDTO(
            "João Cliente Atualizado",
            "cliente@email.com",
            "11999999999",
            "novaSenha123",
            "12345678901",
            "Novo Endereço, 789"
        );

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCliente))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Cliente Atualizado"))
                .andExpect(jsonPath("$.endereco").value("Novo Endereço, 789"));
    }

    @Test
    void remocoes_DevePermitirExclusoesValidas() throws Exception {
        // Registrar cliente
        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Verificar que existe
        mockMvc.perform(get("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isOk());

        // Deletar cliente
        mockMvc.perform(delete("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verificar que não existe mais
        mockMvc.perform(get("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void filtrosEBuscas_DeveFuncionarCorretamente() throws Exception {
        // Registrar múltiplos clientes
        ClienteRegistroDTO cliente2 = new ClienteRegistroDTO(
            "Maria Cliente",
            "maria@email.com",
            "11888888888",
            "senha123",
            "98765432101",
            "Rua Maria, 456"
        );

        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente2))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Buscar por nome
        mockMvc.perform(get("/api/clientes/buscar?nome=João")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Cliente"));

        // Contar clientes
        mockMvc.perform(get("/api/clientes/count")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void tratamentoErros_DeveRetornarMensagensApropriadas() throws Exception {
        // Tentar buscar cliente inexistente
        mockMvc.perform(get("/api/clientes/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        // Tentar atualizar cliente inexistente
        mockMvc.perform(put("/api/clientes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO))
                .with(csrf()))
                .andExpect(status().isNotFound());

        // Tentar deletar cliente inexistente
        mockMvc.perform(delete("/api/clientes/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void validacaoDados_DeveRejeitarEntradasInvalidas() throws Exception {
        // DTO com dados inválidos
        ClienteRegistroDTO invalidDTO = new ClienteRegistroDTO(
            "", // nome vazio
            "email-invalido", // email inválido
            "", // telefone vazio
            "", // senha vazia
            "cpf-invalido", // CPF inválido
            "" // endereço vazio
        );

        mockMvc.perform(post("/api/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
