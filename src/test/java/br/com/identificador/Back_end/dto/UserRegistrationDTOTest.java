package br.com.identificador.Back_end.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validacao_DevePassarQuandoTodosCamposValidos() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void validacao_DeveFalharQuandoNomeVazio() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "",
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Nome é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoNomeNulo() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            null,
            "joao@email.com",
            "11999999999",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Nome é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoEmailInvalido() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "João Silva",
            "email-invalido",
            "11999999999",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email deve ser válido");
    }

    @Test
    void validacao_DeveFalharQuandoEmailVazio() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "João Silva",
            "",
            "11999999999",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoTelefoneVazio() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "",
            "senha123"
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Telefone é obrigatório");
    }

    @Test
    void validacao_DeveFalharQuandoSenhaVazia() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "João Silva",
            "joao@email.com",
            "11999999999",
            ""
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Senha é obrigatória");
    }

    @Test
    void validacao_DeveFalharQuandoMultiplosCamposInvalidos() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "",
            "email-invalido",
            "",
            ""
        );

        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(4);
    }
}
