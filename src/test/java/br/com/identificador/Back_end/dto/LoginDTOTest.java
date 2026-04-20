package br.com.identificador.Back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validacao_DevePassarQuandoCamposValidos() {
        LoginDTO dto = new LoginDTO("joao@email.com", "senha123");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void validacao_DeveFalharQuandoEmailVazio() {
        LoginDTO dto = new LoginDTO("", "senha123");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoEmailNulo() {
        LoginDTO dto = new LoginDTO(null, "senha123");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoEmailInvalido() {
        LoginDTO dto = new LoginDTO("email-invalido", "senha123");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email deve ser válido");
    }

    @Test
    void validacao_DeveFalharQuandoSenhaVazia() {
        LoginDTO dto = new LoginDTO("joao@email.com", "");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Senha é obrigatória");
    }

    @Test
    void validacao_DeveFalharQuandoSenhaNula() {
        LoginDTO dto = new LoginDTO("joao@email.com", null);

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Senha é obrigatória");
    }

    @Test
    void validacao_DeveFalharQuandoAmbosCamposInvalidos() {
        LoginDTO dto = new LoginDTO("", "");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);
    }
}
