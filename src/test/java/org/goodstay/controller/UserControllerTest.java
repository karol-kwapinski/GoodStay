package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goodstay.dto.*;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private RegisterRequestDto createValidRequest() {
        return new RegisterRequestDto(
                "adam@gmail.com",
                "pass1234",
                "pass1234",
                "Adam",
                "Nowak",
                "777888999",
                "Poland"
        );
    }

    static Stream<Arguments> invalidRegisterRequests() {
        return Stream.of(
                Arguments.of(
                        "empty email",
                        new RegisterRequestDto(
                                "",
                                "pass1234",
                                "pass1234",
                                "Adam",
                                "Nowak",
                                "777888999",
                                "Poland"
                        )
                ),
                Arguments.of(
                        "empty password",
                        new RegisterRequestDto(
                                "adam@gmail.com",
                                "",
                                "",
                                "Adam",
                                "Nowak",
                                "777888999",
                                "Poland"
                        )
                ),
                Arguments.of(
                        "invalid email format",
                        new RegisterRequestDto(
                                "adamgmail.com",
                                "pass1234",
                                "pass1234",
                                "Adam",
                                "Nowak",
                                "777888999",
                                "Poland"
                        )
                ),
                Arguments.of(
                        "invalid password format",
                        new RegisterRequestDto(
                                "adam@gmail.com",
                                "pass",
                                "pass",
                                "Adam",
                                "Nowak",
                                "777888999",
                                "Poland"
                        )
                )
        );
    }

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequestDto request = createValidRequest();

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService).register(request);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRegisterRequests")
    void shouldThrow400WhenRequestIsInvalid(String description,
                                            RegisterRequestDto request) throws Exception {

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldThrow409WhenEmailAlreadyExists() throws Exception {

        RegisterRequestDto request = createValidRequest();

        doThrow(new EmailAlreadyExistsException())
                .when(userService)
                .register(request);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginUser() throws Exception {

        LoginRequestDto request = new LoginRequestDto(
                "jan.nowak@gmail.com",
                "pass1234"
        );

        CurrentUserDto currentUser = new CurrentUserDto(
                "jan.nowak@gmail.com",
                "Jan",
                "Nowak",
                "333444555",
                "Poland",
                "USER"
        );

        LoginResultDto result = new LoginResultDto(
                "validToken",
                        currentUser,
                        3600000
        );

        when(userService.login(request))
                .thenReturn(result);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).login(request);

    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {

        LoginRequestDto request = new LoginRequestDto(
                "jan.nowak@gmail.com",
                "pass1234"
        );

        when(userService.login(request))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService).login(request);
    }

    @Test
    void shouldLogoutUser() throws Exception {

        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        CurrentUserDto dto = new CurrentUserDto(
                "jan.nowak@gmail.com",
                "Jan",
                "Nowak",
                "333444555",
                "Poland",
                "USER"
        );

        when(userService.getCurrentUser(any(Authentication.class)))
                .thenReturn(dto);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.nowak@gmail.com",
                null
        );

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("jan.nowak@gmail.com"));

        verify(userService).getCurrentUser(any(Authentication.class));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotInDatabase() throws Exception {

        when(userService.getCurrentUser(any(Authentication.class)))
                .thenThrow(new NoSuchElementException());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.kowalski2@gmail.com",
                null
        );

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
                .andExpect(status().isNotFound());

        verify(userService).getCurrentUser(any(Authentication.class));
    }

    @Test
    void shouldReturnAllHotelOwnersEmails() throws Exception {

        when(userService.getHotelAllOwners())
                .thenReturn(List.of(
                        new HotelOwnerResponseDto(
                                1L,
                                "jan.kowalski@gmail.com"
                        ),
                        new HotelOwnerResponseDto(
                                2L,
                                "adam.nowak@gmail.com"
                        )
                ));

        mockMvc.perform(get("/api/users/getAllHotelOwnersEmails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email")
                        .value("jan.kowalski@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email")
                        .value("adam.nowak@gmail.com"));

        verify(userService).getHotelAllOwners();
    }

}