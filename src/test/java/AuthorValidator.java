import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorValidator {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAuthorFirstNameNotEmpty() {
        NewBookAuthor newBookAuthor = new NewBookAuthor("", "Doe");
        Set<ConstraintViolation<NewBookAuthor>> violations = validator.validate(newBookAuthor);
        assertEquals(1, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath().toString() + ": " + v.getMessage()));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("authorFirstName")));
    }

    @Test
    public void testAuthorLastNameNotEmpty() {
        NewBookAuthor newBookAuthor = new NewBookAuthor("John", "");
        Set<ConstraintViolation<NewBookAuthor>> violations = validator.validate(newBookAuthor);
        assertEquals(1, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath().toString() + ": " + v.getMessage()));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("authorLastName")));
    }

    @Test
    public void testValidAuthor() {
        NewBookAuthor newBookAuthor = new NewBookAuthor("John", "Doe");
        Set<ConstraintViolation<NewBookAuthor>> violations = validator.validate(newBookAuthor);
        violations.forEach(v -> System.out.println(v.getPropertyPath().toString() + ": " + v.getMessage()));
        assertTrue(violations.isEmpty());
    }
}