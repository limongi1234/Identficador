package br.com.identificador.Back_end.service;

import br.com.identificador.Back_end.dto.LoginDTO;
import br.com.identificador.Back_end.dto.UserRegistrationDTO;
import br.com.identificador.Back_end.model.User;
import br.com.identificador.Back_end.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
        testUser.setSenha("hashedPassword");
        testUser.setCreatedAt(LocalDateTime.now());

        registrationDTO = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        loginDTO = new LoginDTO("joao@email.com", "senha123");
    }

    @Test
    void register_DeveCriarUsuarioComSucesso() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(registrationDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DeveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registrationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateLogin_DeveRetornarUsuarioQuandoCredenciaisValidas() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("senha123", "hashedPassword")).thenReturn(true);

        Optional<User> result = userService.validateLogin(loginDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao@email.com");

        verify(userRepository).findByEmail("joao@email.com");
        verify(passwordEncoder).matches("senha123", "hashedPassword");
    }

    @Test
    void validateLogin_DeveRetornarEmptyQuandoEmailNaoEncontrado() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.validateLogin(loginDTO);

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("joao@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void validateLogin_DeveRetornarEmptyQuandoSenhaIncorreta() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("senha123", "hashedPassword")).thenReturn(false);

        Optional<User> result = userService.validateLogin(loginDTO);

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("joao@email.com");
        verify(passwordEncoder).matches("senha123", "hashedPassword");
    }

    @Test
    void findById_DeveRetornarUsuarioQuandoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);

        verify(userRepository).findById(1L);
    }

    @Test
    void findById_DeveRetornarEmptyQuandoNaoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1L);

        assertThat(result).isEmpty();

        verify(userRepository).findById(1L);
    }

    @Test
    void findByEmail_DeveRetornarUsuarioQuandoEncontrado() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("joao@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao@email.com");

        verify(userRepository).findByEmail("joao@email.com");
    }

    @Test
    void findAll_DeveRetornarListaDeUsuarios() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");

        verify(userRepository).findAll();
    }

    @Test
    void findByNomeContaining_DeveRetornarUsuariosFiltrados() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNomeContaining("João")).thenReturn(users);

        List<User> result = userService.findByNomeContaining("João");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");

        verify(userRepository).findByNomeContaining("João");
    }

    @Test
    void findUsersCreatedAfter_DeveRetornarUsuariosFiltradosPorData() {
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findUsersCreatedAfter(date)).thenReturn(users);

        List<User> result = userService.findUsersCreatedAfter(date);

        assertThat(result).hasSize(1);

        verify(userRepository).findUsersCreatedAfter(date);
    }

    @Test
    void count_DeveRetornarTotalDeUsuarios() {
        when(userRepository.count()).thenReturn(10L);

        long result = userService.count();

        assertThat(result).isEqualTo(10L);

        verify(userRepository).count();
    }

    @Test
    void getAllEmails_DeveRetornarListaDeEmails() {
        List<String> emails = Arrays.asList("joao@email.com", "maria@email.com");
        when(userRepository.findAllEmails()).thenReturn(emails);

        List<String> result = userService.getAllEmails();

        assertThat(result).hasSize(2);
        assertThat(result).contains("joao@email.com", "maria@email.com");

        verify(userRepository).findAllEmails();
    }

    @Test
    void update_DeveAtualizarUsuarioComSucesso() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setNome("João Silva Atualizado");
        updatedUser.setEmail("joao@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("joao@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.update(1L, registrationDTO);

        assertThat(result.getNome()).isEqualTo("João Silva Atualizado");

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmailAndIdNot("joao@email.com", 1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_DeveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(1L, registrationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_DeveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("joao@email.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, registrationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já está em uso por outro usuário");

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmailAndIdNot("joao@email.com", 1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_DeveDeletarUsuarioComSucesso() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_DeveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void loadUserByUsername_DeveRetornarUserDetailsQuandoEncontrado() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(testUser));

        UserDetails result = userService.loadUserByUsername("joao@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("joao@email.com");

        verify(userRepository).findByEmail("joao@email.com");
    }

    @Test
    void loadUserByUsername_DeveLancarExcecaoQuandoNaoEncontrado() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("joao@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: joao@email.com");

        verify(userRepository).findByEmail("joao@email.com");
    }
}
