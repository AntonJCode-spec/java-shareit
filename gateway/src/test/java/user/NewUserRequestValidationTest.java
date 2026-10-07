package user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NewUserRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldPassWhenAllFieldsValid() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Oliver");
        request.setEmail("oliver@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        NewUserRequest request = new NewUserRequest();
        request.setName("");
        request.setEmail("oliver@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("имя не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsNull() {
        NewUserRequest request = new NewUserRequest();
        request.setName(null);
        request.setEmail("oliver@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Oliver");
        request.setEmail("not-an-email");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("email не корректный");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Oliver");
        request.setEmail("");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFailWhenBothFieldsInvalid() {
        NewUserRequest request = new NewUserRequest();
        request.setName(null);
        request.setEmail(null);

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
    }
}
