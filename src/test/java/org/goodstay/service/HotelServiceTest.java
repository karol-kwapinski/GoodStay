package org.goodstay.service;

import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelListResponseDto;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.mapper.HotelMapper;
import org.goodstay.model.Hotel;
import org.goodstay.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Test
    void shouldReturnAllAvailableHotels() {

        Hotel hotel = new Hotel();

        List<Hotel> listOfHotels = List.of(hotel);

        HotelListRequestDto request = new HotelListRequestDto(
                "Warsaw",
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 30)
        );

        HotelListResponseDto response = new HotelListResponseDto(
                1L,
                "WarsawHotel",
            "Warsaw",
            "Mickiewicza",
            "24B",
            3,
            0
        );

        when(hotelRepository.getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate()))
                .thenReturn(listOfHotels);

        when(hotelMapper.toDto(listOfHotels))
                .thenReturn(List.of(response));

        List<HotelListResponseDto> hotelList = hotelService.getAvailableHotels(request);

        assertEquals(List.of(response), hotelList);
        assertEquals(1, hotelList.size());
        assertSame(response, hotelList.getFirst());

        verify(hotelRepository).getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate());

        verify(hotelMapper).toDto(listOfHotels);
    }

    @Test
    void shouldThrowInvalidDateRangeException() {

        HotelListRequestDto request = new HotelListRequestDto(
                "Cracow",
                LocalDate.of(2026, 8, 30),
                LocalDate.of(2026, 8, 15)
        );

        assertThrows(InvalidDateRangeException.class,
                () -> hotelService.getAvailableHotels(request));

        verify(hotelRepository, never()).getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate()
        );

        verify(hotelMapper, never()).toDto(anyList());
    }
}
