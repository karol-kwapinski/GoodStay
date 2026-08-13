package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goodstay.dto.RoomListRequestDto;
import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    static Stream<Arguments> returnInvalidRequests() {
        return Stream.of(
                Arguments.of(
                        "Invalid check in date",
                        1L,
                        new RoomListRequestDto(
                                LocalDate.now().minusDays(5),
                                LocalDate.now().plusDays(1)
                        )
                ),
                Arguments.of(
                        "Invalid check out date",
                        1L,
                        new RoomListRequestDto(
                                LocalDate.now(),
                                LocalDate.now().minusDays(5)
                        )
                )
        );
    }

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(roomController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnRoomsByDatesAndHotelId() throws Exception {

        RoomListRequestDto request = new RoomListRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        RoomListResponseDto response = new RoomListResponseDto(
                1L,
                BigDecimal.valueOf(150.00),
                "Bedroom",
                3
        );

        when(roomService.getAllRoomsByDatesAndHotelId(
                1L,
                request
        ))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/rooms/getAllRoomsByDatesAndHotelId/{hotelId}", 1L)
                .param("checkInDate", request.checkInDate().toString())
                .param("checkOutDate", request.checkOutDate().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(response.id()))
                    .andExpect(jsonPath("$[0].pricePerNight")
                            .value(response.pricePerNight()))
                    .andExpect(jsonPath("$[0].roomType")
                            .value(response.roomType()))
                    .andExpect(jsonPath("$[0].maxNumberOfGuests")
                            .value(response.maxNumberOfGuests()));

        verify(roomService).getAllRoomsByDatesAndHotelId(
                1L,
                request
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("returnInvalidRequests")
    void shouldReturnBadRequest(
            String description,
            Long hotelId,
            RoomListRequestDto request) throws Exception {

        mockMvc.perform(get("/api/rooms/getAllRoomsByDatesAndHotelId/{hotelId}", hotelId)
                .param("checkInDate", request.checkInDate().toString())
                .param("checkOutDate", request.checkOutDate().toString()))
                    .andExpect(status().isBadRequest());

        verifyNoInteractions(roomService);
    }

    @Test
    void shouldReturnBadRequestWhenCheckOutDateIsBeforeCheckInDate() throws Exception {

        RoomListRequestDto request = new RoomListRequestDto(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2)
        );

        when(roomService.getAllRoomsByDatesAndHotelId(
                1L,
                request
        ))
                .thenThrow(new InvalidDateRangeException());

        mockMvc.perform(get("/api/rooms/getAllRoomsByDatesAndHotelId/{hotelId}",
                1L)
                .param("checkInDate", request.checkInDate().toString())
                .param("checkOutDate", request.checkOutDate().toString()))
                    .andExpect(status().isBadRequest());

        verify(roomService).getAllRoomsByDatesAndHotelId(
                1L,
                request
        );
    }
}
