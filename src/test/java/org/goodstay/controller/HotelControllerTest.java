package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelListResponseDto;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.service.HotelService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private HotelController hotelController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    static Stream<Arguments> invalidGetHotelsRequests() {
        return Stream.of(
            Arguments.of(
                        "Invalid checkInDate",
                        new HotelListRequestDto(
                                "Warsaw",
                                LocalDate.now().minusDays(5),
                                LocalDate.now().plusDays(1)
                        )
                    ),
                    Arguments.of(
                        "Invalid checkOutDate",
                        new HotelListRequestDto(
                                "Cracow",
                                LocalDate.now(),
                                LocalDate.now().minusDays(5)
                        )
                    ),
                    Arguments.of(
                        "Blank city name field",
                        new HotelListRequestDto(
                                "",
                                LocalDate.now().plusDays(3),
                                LocalDate.now().plusDays(8)
                        )
                    )
            );
    }

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(hotelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnHotelList() throws Exception {

        HotelListRequestDto request = new HotelListRequestDto(
                "Warsaw",
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 25)
        );

        HotelListResponseDto response = new HotelListResponseDto(
                1L,
                "WarsawHotel",
                "Warsaw",
                "Mickiewicza",
                "12A",
                4,
                0
        );

        when(hotelService.getAvailableHotels(request))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/hotels/getAllHotelsByResAndCity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("WarsawHotel"))
                .andExpect(jsonPath("$[0].cityName")
                        .value("Warsaw"))
                .andExpect(jsonPath("$[0].street")
                .value("Mickiewicza"))
                .andExpect(jsonPath("$[0].buildingNumber")
                        .value("12A"))
                .andExpect(jsonPath("$[0].stars")
                .value(4))
                .andExpect(jsonPath("$[0].numberOfRatings")
                        .value(0));

        verify(hotelService).getAvailableHotels(request);
    }

    @MethodSource("invalidGetHotelsRequests")
    @ParameterizedTest(name = "{0}")
    void shouldThrowBadRequestWhenDatesOrCityNameIsInvalid(
            String description, HotelListRequestDto request) throws Exception {

        mockMvc.perform(post("/api/hotels/getAllHotelsByResAndCity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowBadRequestWhenCheckoutDateIsBeforeCheckinDate() throws Exception {

            HotelListRequestDto request = new HotelListRequestDto(
                        "Cracow",
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(3)
                );

            when(hotelService.getAvailableHotels(request))
                    .thenThrow(new InvalidDateRangeException());

            mockMvc.perform(post("/api/hotels/getAllHotelsByResAndCity")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(hotelService).getAvailableHotels(request);
    }

}
