package org.goodstay.service;

import org.goodstay.dto.RegisterRequestDTO;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.respository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequestDTO createValidRequest() {

        return new RegisterRequestDTO(
                "adam.kowalski@interia.pl",
                "pass1234",
                "pass1234",
                "Adam",
                "Kowalski",
                "555655777",
                "Poland"
        );
    }

    static Stream<RegisterRequestDTO> registerRequests() {

        return Stream.of(
                new RegisterRequestDTO(
                        "jan.kowalski@gmail.com",
                        "pass1234",
                        "pass1234",
                        "",
                        "",
                        "",
                        ""
                ),
                new RegisterRequestDTO(
                        "agata.kowalska@gmail.com",
                        "passABCD",
                        "passABCD",
                        "Agata",
                        "Kowalska",
                        "123456789",
                        "Poland"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("registerRequests")
    void shouldSaveNewUser(RegisterRequestDTO request) {

        userService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowPasswordsDoNotMatchException() {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "daniel.kowalski1@interia.pl",
                "pass1234",
                "abcd",
                "",
                "",
                "",
                ""
        );

        assertThrows(PasswordMismatchException.class,
                () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldSaveUserWithCorrectData() {

        RegisterRequestDTO request = createValidRequest();

        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(request.password()))
                .thenReturn(encodedPassword);

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        userService.register(request);

        verify(passwordEncoder).encode(request.password());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals(request.email(), savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals(request.firstName(), savedUser.getFirstName());
        assertEquals(request.lastName(), savedUser.getLastName());
        assertEquals(request.phoneNumber(), savedUser.getPhoneNumber());
        assertEquals(request.country(), savedUser.getCountry());
        assertEquals(UserRole.USER, savedUser.getRole());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {

        RegisterRequestDTO request = createValidRequest();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

}