package item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NewItemRequestValidationTest {

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
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная дрель");
        request.setAvailable(true);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        NewItemRequest request = new NewItemRequest();
        request.setName("");
        request.setDescription("Мощная дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Имя не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("");
        request.setAvailable(true);

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void shouldFailWhenAvailableIsNull() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная дрель");
        request.setAvailable(null);

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void shouldNotFailWhenRequestIdIsNull() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная дрель");
        request.setAvailable(true);
        request.setRequestId(null);

        assertThat(validator.validate(request)).isEmpty();
    }
}
