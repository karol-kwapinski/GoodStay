package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goodstay.dto.RegisterRequestDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private RegisterRequestDTO createValidRequest() {
        return new RegisterRequestDTO(
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
                        new RegisterRequestDTO(
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
                        new RegisterRequestDTO(
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
                        new RegisterRequestDTO(
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
                        new RegisterRequestDTO(
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
        RegisterRequestDTO request = createValidRequest();

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService).register(request);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRegisterRequests")
    void shouldThrow400WhenRequestIsInvalid(String description,
                                            RegisterRequestDTO request) throws Exception {

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldThrow409WhenEmailAlreadyExists() throws Exception {

        RegisterRequestDTO request = createValidRequest();

        doThrow(new EmailAlreadyExistsException())
                .when(userService)
                .register(request);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}