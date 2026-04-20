package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.ClienteRegistroDTO;
import br.com.identificador.Back_end.model.Cliente;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.repository.ClienteRepository;
import br.com.identificador.Back_end.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente testCliente;
    private User testUser;
    private ClienteRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNome("João Silva");
        testUser.setEmail("joao@email.com");
        testUser.setTelefone("11999999999");
        testUser.setSenha("hashedPassword");

        testCliente = new Cliente();
        testCliente.setId(1L);
        testCliente.setCpf("12345678901");
        testCliente.setEndereco("Rua das Flores, 123");

        registroDTO = new ClienteRegistroDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123",
            "12345678901",
            "Rua das Flores, 123"
        );
    }

    @Test
    void registrarCliente_DeveCriarClienteComSucesso() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(testCliente);

        Cliente result = clienteService.registrarCliente(registroDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("12345678901");
        assertThat(result.getEndereco()).isEqualTo("Rua das Flores, 123");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(clienteRepository).existsByCpf("12345678901");
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void registrarCliente_DeveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.registrarCliente(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(clienteRepository, never()).existsByCpf(anyString());
    }

    @Test
    void registrarCliente_DeveLancarExcecaoQuandoCpfJaExiste() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.registrarCliente(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(clienteRepository).existsByCpf("12345678901");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void buscarPorId_DeveRetornarClienteQuandoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(testCliente));

        Optional<Cliente> result = clienteService.buscarPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCpf()).isEqualTo("12345678901");

        verify(clienteRepository).findById(1L);
    }

    @Test
    void buscarPorCpf_DeveRetornarClienteQuandoEncontrado() {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(testCliente));

        Optional<Cliente> result = clienteService.buscarPorCpf("12345678901");

        assertThat(result).isPresent();
        assertThat(result.get().getCpf()).isEqualTo("12345678901");

        verify(clienteRepository).findByCpf("12345678901");
    }

    @Test
    void buscarPorEmail_DeveRetornarClienteQuandoEncontrado() {
        when(clienteRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testCliente));

        Optional<Cliente> result = clienteService.buscarPorEmail("joao@email.com");

        assertThat(result).isPresent();

        verify(clienteRepository).findByEmail("joao@email.com");
    }

    @Test
    void listarTodos_DeveRetornarListaDeClientes() {
        List<Cliente> clientes = Arrays.asList(testCliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> result = clienteService.listarTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpf()).isEqualTo("12345678901");

        verify(clienteRepository).findAll();
    }

    @Test
    void buscarPorNome_DeveRetornarClientesFiltrados() {
        List<Cliente> clientes = Arrays.asList(testCliente);
        when(clienteRepository.findByNomeContaining("João")).thenReturn(clientes);

        List<Cliente> result = clienteService.buscarPorNome("João");

        assertThat(result).hasSize(1);

        verify(clienteRepository).findByNomeContaining("João");
    }

    @Test
    void atualizarCliente_DeveAtualizarComSucesso() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(1L);
        clienteAtualizado.setCpf("12345678901");
        clienteAtualizado.setEndereco("Novo Endereço, 456");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(testCliente));
        when(clienteRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        Cliente result = clienteService.atualizarCliente(1L, registroDTO);

        assertThat(result.getEndereco()).isEqualTo("Novo Endereço, 456");

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByCpfAndIdNot("12345678901", 1L);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_DeveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizarCliente(1L, registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado");

        verify(clienteRepository).findById(1L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_DeveLancarExcecaoQuandoCpfJaExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(testCliente));
        when(clienteRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.atualizarCliente(1L, registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já está em uso por outro cliente");

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByCpfAndIdNot("12345678901", 1L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deletarCliente_DeveRemoverClienteComSucesso() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.deletarCliente(1L);

        verify(clienteRepository).existsById(1L);
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void deletarCliente_DeveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletarCliente(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado");

        verify(clienteRepository).existsById(1L);
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void count_DeveRetornarTotalDeClientes() {
        when(clienteRepository.count()).thenReturn(5L);

        long result = clienteService.count();

        assertThat(result).isEqualTo(5L);

        verify(clienteRepository).count();
    }
}
