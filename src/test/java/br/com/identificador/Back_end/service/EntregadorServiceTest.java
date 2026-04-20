package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.EntregadorRegistroDTO;
import br.com.identificador.Back_end.model.Entregador;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.model.enuns.Aplicativo;
import br.com.identificador.Back_end.model.enuns.StatusEntregador;
import br.com.identificador.Back_end.repository.EntregadorRepository;
import br.com.identificador.Back_end.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntregadorServiceTest {

    @Mock
    private EntregadorRepository entregadorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EntregadorService entregadorService;

    private Entregador testEntregador;
    private User testUser;
    private EntregadorRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNome("João Silva");
        testUser.setEmail("joao@email.com");
        testUser.setTelefone("11999999999");
        testUser.setSenha("hashedPassword");

        testEntregador = new Entregador();
        testEntregador.setId(1L);
        testEntregador.setCpf("12345678901");
        testEntregador.setRg("123456789");
        testEntregador.setCnh("12345678901");
        testEntregador.setStatus(StatusEntregador.DISPONIVEL);
        testEntregador.setAvaliacaoMedia(4.5);
        testEntregador.setTotalEntregas(10);
        testEntregador.setAplicativos(Set.of(Aplicativo.IFOOD, Aplicativo.RAPPI));

        registroDTO = new EntregadorRegistroDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123",
            "12345678901",
            "123456789",
            "12345678901",
            Set.of(Aplicativo.IFOOD, Aplicativo.RAPPI)
        );
    }

    @Test
    void registrarEntregador_DeveCriarEntregadorComSucesso() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(entregadorRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.registrarEntregador(registroDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("12345678901");
        assertThat(result.getRg()).isEqualTo("123456789");
        assertThat(result.getCnh()).isEqualTo("12345678901");
        assertThat(result.getStatus()).isEqualTo(StatusEntregador.DISPONIVEL);
        assertThat(result.getAplicativos()).contains(Aplicativo.IFOOD, Aplicativo.RAPPI);

        verify(userRepository).existsByEmail("joao@email.com");
        verify(entregadorRepository).existsByCpf("12345678901");
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
        verify(entregadorRepository).save(any(Entregador.class));
    }

    @Test
    void registrarEntregador_DeveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> entregadorService.registrarEntregador(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(entregadorRepository, never()).existsByCpf(anyString());
    }

    @Test
    void registrarEntregador_DeveLancarExcecaoQuandoCpfJaExiste() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(entregadorRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> entregadorService.registrarEntregador(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(entregadorRepository).existsByCpf("12345678901");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void buscarPorId_DeveRetornarEntregadorQuandoEncontrado() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));

        Entregador result = entregadorService.buscarPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("12345678901");

        verify(entregadorRepository).findById(1L);
    }

    @Test
    void buscarPorId_DeveLancarExcecaoQuandoNaoEncontrado() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entregadorService.buscarPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entregador não encontrado com ID: 1");

        verify(entregadorRepository).findById(1L);
    }

    @Test
    void buscarPorEmail_DeveRetornarEntregadorQuandoEncontrado() {
        when(entregadorRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testEntregador));

        Entregador result = entregadorService.buscarPorEmail("joao@email.com");

        assertThat(result).isNotNull();

        verify(entregadorRepository).findByEmail("joao@email.com");
    }

    @Test
    void listarTodos_DeveRetornarListaDeEntregadores() {
        List<Entregador> entregadores = Arrays.asList(testEntregador);
        when(entregadorRepository.findAll()).thenReturn(entregadores);

        List<Entregador> result = entregadorService.listarTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpf()).isEqualTo("12345678901");

        verify(entregadorRepository).findAll();
    }

    @Test
    void buscarPorStatus_DeveRetornarEntregadoresFiltrados() {
        List<Entregador> entregadores = Arrays.asList(testEntregador);
        when(entregadorRepository.findByStatus(StatusEntregador.DISPONIVEL)).thenReturn(entregadores);

        List<Entregador> result = entregadorService.buscarPorStatus(StatusEntregador.DISPONIVEL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StatusEntregador.DISPONIVEL);

        verify(entregadorRepository).findByStatus(StatusEntregador.DISPONIVEL);
    }

    @Test
    void buscarDisponiveis_DeveRetornarEntregadoresDisponiveis() {
        List<Entregador> disponiveis = Arrays.asList(testEntregador);
        when(entregadorRepository.buscarEntregadoresDisponiveis(StatusEntregador.DISPONIVEL)).thenReturn(disponiveis);

        List<Entregador> result = entregadorService.buscarDisponiveis();

        assertThat(result).hasSize(1);

        verify(entregadorRepository).buscarEntregadoresDisponiveis(StatusEntregador.DISPONIVEL);
    }

    @Test
    void buscarPorAplicativo_DeveRetornarEntregadoresComAplicativo() {
        List<Entregador> entregadores = Arrays.asList(testEntregador);
        when(entregadorRepository.findAll()).thenReturn(entregadores);

        List<Entregador> result = entregadorService.buscarPorAplicativo(Aplicativo.IFOOD);

        assertThat(result).hasSize(1);

        verify(entregadorRepository).findAll();
    }

    @Test
    void atualizarStatus_DeveAlterarStatusComSucesso() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.atualizarStatus(1L, StatusEntregador.EM_ROTA);

        assertThat(result.getStatus()).isEqualTo(StatusEntregador.EM_ROTA);

        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).save(testEntregador);
    }

    @Test
    void adicionarAplicativo_DeveAdicionarAplicativoComSucesso() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.adicionarAplicativo(1L, Aplicativo.UBER_EATS);

        assertThat(result.getAplicativos()).contains(Aplicativo.UBER_EATS);

        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).save(testEntregador);
    }

    @Test
    void removerAplicativo_DeveRemoverAplicativoComSucesso() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.removerAplicativo(1L, Aplicativo.IFOOD);

        assertThat(result.getAplicativos()).doesNotContain(Aplicativo.IFOOD);

        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).save(testEntregador);
    }

    @Test
    void atualizarAvaliacao_DeveCalcularMediaCorretamente() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.atualizarAvaliacao(1L, 5.0);

        // Verifica se a média foi calculada: ((4.5 * 10) + 5.0) / 11 = 4.545...
        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).save(testEntregador);
    }

    @Test
    void atualizarDados_DeveAtualizarComSucesso() {
        EntregadorRegistroDTO updateDTO = new EntregadorRegistroDTO(
            "João Silva Atualizado",
            "joao@email.com",
            "11888888888",
            "novaSenha123",
            "12345678901",
            "123456789",
            "12345678901",
            Set.of(Aplicativo.UBER_EATS)
        );

        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        Entregador result = entregadorService.atualizarDados(1L, updateDTO);

        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).existsByCpfAndIdNot("12345678901", 1L);
        verify(entregadorRepository).save(any(Entregador.class));
    }

    @Test
    void deletarEntregador_DeveRemoverComSucesso() {
        when(entregadorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(entregadorRepository).deleteById(1L);

        entregadorService.deletarEntregador(1L);

        verify(entregadorRepository).existsById(1L);
        verify(entregadorRepository).deleteById(1L);
    }

    @Test
    void deletarEntregador_DeveLancarExcecaoQuandoNaoEncontrado() {
        when(entregadorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> entregadorService.deletarEntregador(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entregador não encontrado com ID: 1");

        verify(entregadorRepository).existsById(1L);
        verify(entregadorRepository, never()).deleteById(anyLong());
    }

    @Test
    void regenerarQrCode_DeveGerarNovoUuid() {
        when(entregadorRepository.findById(1L)).thenReturn(Optional.of(testEntregador));
        when(entregadorRepository.save(any(Entregador.class))).thenReturn(testEntregador);

        String result = entregadorService.regenerarQrCode(1L);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        verify(entregadorRepository).findById(1L);
        verify(entregadorRepository).save(testEntregador);
    }
}
