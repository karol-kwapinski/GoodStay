package org.goodstay.integrationTests;

import jakarta.transaction.Transactional;
import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.dto.HotelOwnerResponseDto;
import org.goodstay.dto.LoginRequestDto;
import org.goodstay.dto.LoginResultDto;
import org.goodstay.dto.RegisterRequestDto;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.repository.UserRepository;
import org.goodstay.service.UserService;
import org.goodstay.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {
        ApplicationConfiguration.class,
        TestDataFactory.class
    }
)
@TestPropertySource("classpath:application-test.properties")
@Transactional
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    private RegisterRequestDto createValidRegisterRequestDto() {
        return new RegisterRequestDto(
                "adam@gmail.com",
                "password123",
                "password123",
                "Adam",
                "Kowalski",
                "123456789",
                "Poland"
        );
    }

    @Test
    void shouldRegisterUser() {

        RegisterRequestDto request = createValidRegisterRequestDto();

        userService.register(request);

        User user = userRepository.findByEmail("adam@gmail.com")
                .orElseThrow();

        assertEquals(request.email(), user.getEmail());
        assertEquals(request.firstName(), user.getFirstName());
        assertEquals(request.lastName(), user.getLastName());
        assertEquals(request.phoneNumber(), user.getPhoneNumber());
        assertEquals(request.country(), user.getCountry());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {

        RegisterRequestDto request = createValidRegisterRequestDto();

        userService.register(request);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(request));
    }

    @Test
    void shouldThrowPasswordMismatchException() {

        RegisterRequestDto request = new RegisterRequestDto(
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

    @Test
    void shouldLoginUser() {
        RegisterRequestDto registerRequest = createValidRegisterRequestDto();

        userService.register(registerRequest);

        LoginRequestDto loginRequest = new LoginRequestDto(
                registerRequest.email(),
                registerRequest.password()
        );

        LoginResultDto dto = userService.login(loginRequest);

        assertEquals(registerRequest.email(), dto.user().email());
        assertEquals(registerRequest.firstName(), dto.user().firstName());
        assertEquals(UserRole.USER.name(), dto.user().role());
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenUserDoesNotExist() {

        LoginRequestDto loginRequest = new LoginRequestDto(
                "jan.kowalski@gmail.com",
                "pass1234"
        );

        assertThrows(BadCredentialsException.class,
                () -> userService.login(loginRequest));

    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenPasswordIsWrong() {

        RegisterRequestDto registerRequest = createValidRegisterRequestDto();

        userService.register(registerRequest);

        LoginRequestDto loginRequest = new LoginRequestDto(
                registerRequest.email(),
                "wrongPassword"
        );

        assertThrows(BadCredentialsException.class,
                () -> userService.login(loginRequest));

    }

    @Test
    void shouldReturnAllHotelOwnersEmails() {
        User owner1 = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.HOTEL_OWNER
        );
        User owner2 = testDataFactory.createUser(
                "jan.nowak@interia.pl",
                UserRole.HOTEL_OWNER
        );
        User owner3 = testDataFactory.createUser(
                "agata.nowakowska@gmail.com",
                UserRole.HOTEL_OWNER
        );

        List<HotelOwnerResponseDto> expected = List.of(
                new HotelOwnerResponseDto(owner1.getId(), owner1.getEmail()),
                new HotelOwnerResponseDto(owner2.getId(), owner2.getEmail()),
                new HotelOwnerResponseDto(owner3.getId(), owner3.getEmail())
        );
        List<HotelOwnerResponseDto> ownerEmails = userService.getHotelAllOwners();

        assertEquals(expected, ownerEmails);
    }

}
