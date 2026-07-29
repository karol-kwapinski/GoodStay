package org.goodstay.integrationTests;

import jakarta.transaction.Transactional;
import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.configuration.TestConfiguration;
import org.goodstay.dto.RegisterRequestDTO;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.repository.UserRepository;
import org.goodstay.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {
        ApplicationConfiguration.class,
        TestConfiguration.class
    }
)
@TestPropertySource("classpath:application-test.properties")
@Transactional
class UserRegistrationIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUser() {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "adam@gmail.com",
                "password123",
                "password123",
                "Adam",
                "Kowalski",
                "123456789",
                "Poland"
        );

        userService.register(request);


        User user = userRepository.findByEmail("adam@gmail.com")
                .orElseThrow();


        assertEquals("adam@gmail.com", user.getEmail());
        assertEquals("Adam", user.getFirstName());
        assertEquals("Kowalski", user.getLastName());
        assertEquals("123456789", user.getPhoneNumber());
        assertEquals("Poland", user.getCountry());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "adam@gmail.com",
                "password123",
                "password123",
                "Adam",
                "Kowalski",
                "123456789",
                "Poland"
        );

        userService.register(request);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(request));
    }

    @Test
    void shouldThrowPasswordMismatchException() {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "adam@gmail.com",
                "password123",
                "password",
                "Adam",
                "Kowalski",
                "123456789",
                "Poland"
        );

        assertThrows(PasswordMismatchException.class,
                () -> userService.register(request));

    }

}
