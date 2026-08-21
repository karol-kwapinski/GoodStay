package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.RoomTypeSelectionDto;
import org.goodstay.dto.TotalPriceDto;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.service.ReservationService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    static Stream<Arguments> getBadRequests() {
        return Stream.of(
                Arguments.of(
                        "Check in date is before current date",
                        new ReservationRequestDto(
                                LocalDate.now().minusDays(3),
                                LocalDate.now().plusDays(1),
                                "Jan",
                                "Kowalski",
                                "jan.kowalski@gmail.com",
                                "333444555",
                                "Poland",
                                3L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        2
                                ))
                        )
                ),
                Arguments.of(
                        "Check out date is before current date",
                        new ReservationRequestDto(
                                LocalDate.now().plusDays(3),
                                LocalDate.now().minusDays(2),
                                "Jan",
                                "Kowalski",
                                "jan.kowalski@gmail.com",
                                "333444555",
                                "Poland",
                                3L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        2
                                ))
                        )
                )
        );
    }

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldAddReservation() throws Exception {

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5),
                "Adam",
                "Kowalski",
                "adam.kowalski@gmail.com",
                "333444555",
                "Poland",
                3L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                ))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "adam.kowalski@gmail.com",
                null
        );

        mockMvc.perform(post("/api/reservations/addReservation")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());

        verify(reservationService).addReservation(request, authentication);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getBadRequests")
    void shouldThrowBadRequest(String description, ReservationRequestDto request) throws Exception {

        mockMvc.perform(post("/api/reservations/addReservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reservationService);
    }

    @Test
    void shouldThrowBadRequestWhenCheckOutDateIsBeforeCheckOutDate() throws Exception {
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2),
                "Adam",
                "Kowalski",
                "adam.kowalski@gmail.com",
                "333444555",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                ))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "adam.kowalski@gmail.com",
                null
        );

        doThrow(new InvalidDateRangeException())
                .when(reservationService)
                        .addReservation(request, authentication);

        mockMvc.perform(post("/api/reservations/addReservation")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

        verify(reservationService).addReservation(request, authentication);
    }

    @Test
    void shouldReturnTotalPrice() throws Exception {
        TotalPriceDto request = new TotalPriceDto(
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5),
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                ),
                new RoomTypeSelectionDto(
                        2L,
                        3
                ))
        );

        when(reservationService.getTotalPrice(1L, request))
                .thenReturn(BigDecimal.valueOf(900.00));

        mockMvc.perform(post("/api/reservations/getTotalPrice/{hotelId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(BigDecimal.valueOf(900.00)));

        verify(reservationService).getTotalPrice(1L, request);
    }
}
