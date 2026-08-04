package org.goodstay.service;


import org.goodstay.dto.CurrentUserDto;
import org.goodstay.dto.LoginRequestDto;
import org.goodstay.dto.RegisterRequestDto;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.repository.UserRepository;
import org.goodstay.security.JWTUtil;
import org.goodstay.util.TestUserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.NoSuchElementException;
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

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private Authentication authentication;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                passwordEncoder,
                jwtUtil,
                3600000
        );
    }

    private RegisterRequestDto createValidRegisterRequest() {

        return new RegisterRequestDto(
                "adam.kowalski@interia.pl",
                "pass1234",
                "pass1234",
                "Adam",
                "Kowalski",
                "555655777",
                "Poland"
        );
    }

    private TestUserData createUserWithLoginRequest() {
        User user = new User();

        user.setId(1L);
        user.setEmail("jan.kowalski@gmail.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Jan");
        user.setRole(UserRole.USER);

        LoginRequestDto request = new LoginRequestDto(
                "jan.kowalski@interia.pl",
                "pass1234"
        );

        return new TestUserData(
                user,
                request
        );

    }

    static Stream<RegisterRequestDto> registerRequests() {

        return Stream.of(
                new RegisterRequestDto(
                        "jan.kowalski@gmail.com",
                        "pass1234",
                        "pass1234",
                        "",
                        "",
                        "",
                        ""
                ),
                new RegisterRequestDto(
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
    void shouldSaveNewUser(RegisterRequestDto request) {

        userService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowPasswordsDoNotMatchException() {

        RegisterRequestDto request = new RegisterRequestDto(
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

        RegisterRequestDto request = createValidRegisterRequest();

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

        RegisterRequestDto request = createValidRegisterRequest();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUser() {

        TestUserData userData = createUserWithLoginRequest();
        LoginRequestDto request = userData.request();
        User user = userData.user();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("jwt-token");

        userService.login(request);

        verify(userRepository).findByEmail(request.email());

        verify(passwordEncoder).matches(request.password(), user.getPassword());

        verify(jwtUtil).generateToken(user);

    }

    @Test
    void shouldNotLoginUserWithIncorrectPassword() {

        TestUserData userData = createUserWithLoginRequest();
        LoginRequestDto request = userData.request();
        User user = userData.user();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.login(request));

        verify(userRepository).findByEmail(request.email());

        verify(passwordEncoder).matches(request.password(), user.getPassword());

        verify(jwtUtil, never()).generateToken(any(User.class));

    }

    @Test
    void shouldNotLoginUserWithIncorrectEmail() {

        TestUserData testUserData = createUserWithLoginRequest();
        LoginRequestDto request = testUserData.request();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> userService.login(request));

        verify(userRepository).findByEmail(request.email());
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
        verify(jwtUtil, never()).generateToken(any(User.class));

    }

    @Test
    void shouldReturnCurrentUser() {
        User user = new User();
        user.setEmail("agata.nowak@gmail.com");
        user.setFirstName("Agata");
        user.setRole(UserRole.USER);

        when(authentication.getName())
                .thenReturn("agata.nowak@gmail.com");
        when(userRepository.findByEmail("agata.nowak@gmail.com"))
                .thenReturn(Optional.of(user));

        CurrentUserDto currentUser = userService.getCurrentUser(authentication);

        assertEquals(user.getEmail(), currentUser.email());
        assertEquals(user.getFirstName(), currentUser.firstName());
        assertEquals(user.getRole().name(), currentUser.role());

        verify(authentication).getName();
        verify(userRepository).findByEmail("agata.nowak@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(authentication.getName())
                .thenReturn("jan.kowalski@interia.pl");

        when(userRepository.findByEmail("jan.kowalski@interia.pl"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getCurrentUser(authentication));
    }

}