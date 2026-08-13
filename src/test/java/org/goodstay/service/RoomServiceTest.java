package org.goodstay.service;

import org.goodstay.dto.RoomListRequestDto;
import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.mapper.RoomMapper;
import org.goodstay.model.Room;
import org.goodstay.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void shouldReturnAllRoomsByDatesAndHotelId() {
        Room room = new Room();

        RoomListResponseDto dto = new RoomListResponseDto(
                1L,
                BigDecimal.valueOf(150.00),
                "bedroom",
                2
        );

        RoomListRequestDto request = new RoomListRequestDto(
                LocalDate.of(2026, 8, 28),
                LocalDate.of(2026, 9, 28)
        );

        when(roomRepository.getAllRoomsByDatesAndHotelId(
                1L,
                request.checkInDate(),
                request.checkOutDate()
        ))
                .thenReturn(List.of(room));

        when(roomMapper.toDto(List.of(room)))
                .thenReturn(List.of(dto));

        List<RoomListResponseDto> response = roomService.getAllRoomsByDatesAndHotelId(
                1L,
                request);

        assertEquals(List.of(dto), response);
        assertEquals(1, List.of(dto).size());
        assertSame(dto, response.getFirst());

        verify(roomRepository).getAllRoomsByDatesAndHotelId(
                1L,
                request.checkInDate(),
                request.checkOutDate()
        );

        verify(roomMapper).toDto(List.of(room));

    }

    @Test
    void shouldThrowInvalidDateRangeException() {

        RoomListRequestDto request = new RoomListRequestDto(
                LocalDate.of(2026, 9, 28),
                LocalDate.of(2026, 8, 28)
        );

        assertThrows(InvalidDateRangeException.class,
                () -> roomService.getAllRoomsByDatesAndHotelId(
                        1L,
                        request));

        verify(roomRepository, never()).getAllRoomsByDatesAndHotelId(
                1L,
                request.checkInDate(),
                request.checkOutDate()
        );

        verify(roomMapper, never()).toDto(anyList());
    }
}
