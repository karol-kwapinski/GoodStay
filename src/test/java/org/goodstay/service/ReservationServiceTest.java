package org.goodstay.service;

import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.RoomTypeSelectionDto;
import org.goodstay.dto.TotalPriceDto;
import org.goodstay.exception.*;
import org.goodstay.mapper.ReservationMapper;
import org.goodstay.model.Reservation;
import org.goodstay.model.ReservationStatus;
import org.goodstay.model.Room;
import org.goodstay.model.User;
import org.goodstay.repository.ReservationRepository;
import org.goodstay.repository.RoomRepository;
import org.goodstay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    static Stream<Arguments> getInvalidReservationRequests() {
        return Stream.of(
                Arguments.of(
                        "Check out date is before check in date",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 16),
                                LocalDate.of(2026, 8, 14),
                                "Agata",
                                "Nowak",
                                "agata.nowak@gmail.com",
                                "123123123",
                                "Poland",
                                1L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ))
                        )
                ),
                Arguments.of(
                        "Check out date is the same as check in date",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 16),
                                LocalDate.of(2026, 8, 16),
                                "Dariusz",
                                "Nowakowski",
                                "dariusz.nowakowski@gmail.com",
                                "444555555",
                                "Poland",
                                1L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ))
                        )
                )
        );
    }

    static Stream<Arguments> getInvalidTotalPriceRequests() {
        return Stream.of(
                Arguments.of(
                        "Check out date is before check in date",
                        new TotalPriceDto(
                                LocalDate.of(2026, 10, 24),
                                LocalDate.of(2026, 10, 20),
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ))
                        )
                ),
                Arguments.of(
                        "Check out date is the same as check in date",
                        new TotalPriceDto(
                                LocalDate.of(2026, 10, 25),
                                LocalDate.of(2026, 10, 25),
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ))
                        )
                )
        );
    }

    static Stream<Arguments> getDuplicateRoomTypeRequests() {
        return Stream.of(
                Arguments.of(
                        "Duplicate room type ids",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 16),
                                LocalDate.of(2026, 8, 24),
                                "Daria",
                                "Nowak",
                                "daria.nowak@gmail.com",
                                "111222333",
                                "Poland",
                                1L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ), new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ))
                        )
                ),
                Arguments.of(
                        "Duplicate room type ids",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 16),
                                LocalDate.of(2026, 8, 24),
                                "Daria",
                                "Nowak",
                                "daria.nowak@gmail.com",
                                "111222333",
                                "Poland",
                                1L,
                                List.of(new RoomTypeSelectionDto(
                                        1L,
                                        1
                                ), new RoomTypeSelectionDto(
                                        2L,
                                        1
                                ), new RoomTypeSelectionDto(
                                        2L,
                                        1
                                ),
                                new RoomTypeSelectionDto(
                                        3L,
                                        1
                                ))
                        )
                )
        );
    }

    @Test
    void shouldAddNewReservation() {

        User user = new User();

        Room room1 = new Room();
        Room room2 = new Room();

        room1.setPricePerNight(BigDecimal.valueOf(100.00));
        room2.setPricePerNight(BigDecimal.valueOf(120.00));

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 18),
                "Jan",
                "Kowalski",
                "jan.kowalski@gmail.com",
                "333444555",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                ))
        );

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setGuestEmail(request.email());
        reservation.setGuestFirstName(request.firstName());
        reservation.setGuestLastName(request.lastName());
        reservation.setGuestCountry(request.country());
        reservation.setGuestPhoneNumber(request.phoneNumber());
        reservation.setStatus(ReservationStatus.PAID);
        reservation.setTotalPrice(BigDecimal.valueOf(440.00));
        reservation.setRooms(List.of(room1, room2));
        reservation.setUser(user);

        when(authentication.getName())
                .thenReturn("jan.kowalski@gmail.com");

        when(userRepository.findByEmail("jan.kowalski@gmail.com"))
                .thenReturn(Optional.of(user));

        when(roomRepository.getAllRoomsByDatesHotelIdAndRoomTypeId(
                request.hotelId(),
                request.roomTypes().getFirst().roomTypeId(),
                request.checkInDate(),
                request.checkOutDate()
        ))
                .thenReturn(List.of(room1, room2));

        when(reservationMapper.toEntity(request, List.of(room1, room2),
                BigDecimal.valueOf(440.00), user))
                .thenReturn(reservation);

        reservationService.addReservation(request, authentication);

        verify(roomRepository).getAllRoomsByDatesHotelIdAndRoomTypeId(
                request.hotelId(),
                request.roomTypes().getFirst().roomTypeId(),
                request.checkInDate(),
                request.checkOutDate()
        );

        verify(reservationMapper).toEntity(request, List.of(room1, room2),
                BigDecimal.valueOf(440.00), user);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(captor.capture());

        Reservation saved = captor.getValue();

        assertEquals(request.checkInDate(), saved.getCheckInDate());
        assertEquals(request.checkOutDate(), saved.getCheckOutDate());
        assertEquals(request.email(), saved.getGuestEmail());
        assertEquals(request.firstName(), saved.getGuestFirstName());
        assertEquals(request.lastName(), saved.getGuestLastName());
        assertEquals(request.country(), saved.getGuestCountry());
        assertEquals(request.phoneNumber(), saved.getGuestPhoneNumber());
        assertEquals(ReservationStatus.PAID, saved.getStatus());
        assertEquals(BigDecimal.valueOf(440.00), saved.getTotalPrice());
        assertEquals(List.of(room1, room2), saved.getRooms());
        assertEquals(user, saved.getUser());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getInvalidReservationRequests")
    void shouldThrowInvalidDateRangeException(
            String description,
            ReservationRequestDto request) {

        User user = new User();

        when(authentication.getName())
                .thenReturn("jan.nowak@gmail.com");

        when(userRepository.findByEmail("jan.nowak@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidDateRangeException.class,
                () -> reservationService.addReservation(request, authentication)
        );

        verifyNoInteractions(roomRepository);
        verifyNoInteractions(reservationMapper);
        verifyNoInteractions(reservationRepository);

    }

    @Test
    void shouldReturnCorrectTotalPrice() {

        Room room1 = new Room();
        room1.setPricePerNight(BigDecimal.valueOf(150.00));

        Room room2 = new Room();
        room2.setPricePerNight(BigDecimal.valueOf(190.00));

        TotalPriceDto dto = new TotalPriceDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 22),
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                ))
        );

        when(roomRepository.getAllRoomsByDatesHotelIdAndRoomTypeId(
                2L,
                dto.roomTypes().getFirst().roomTypeId(),
                dto.checkInDate(),
                dto.checkOutDate()
        ))
                .thenReturn(List.of(room1, room2));

        BigDecimal totalPrice = reservationService.getTotalPrice(2L, dto);

        assertEquals(totalPrice, BigDecimal.valueOf(2040.00));

        verify(roomRepository).getAllRoomsByDatesHotelIdAndRoomTypeId(
                2L,
                dto.roomTypes().getFirst().roomTypeId(),
                dto.checkInDate(),
                dto.checkOutDate()
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidTotalPriceRequests")
    void shouldThrowInvalidDateRangeException(
            String description,
            TotalPriceDto dto
        ) {

        assertThrows(
                InvalidDateRangeException.class,
                () -> reservationService.getTotalPrice(1L, dto)
        );

        verifyNoInteractions(roomRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getDuplicateRoomTypeRequests")
    void shouldThrowInvalidRoomTypeSelectionException(
            String description,
            ReservationRequestDto request
    ) {
        User user = new User();

        when(authentication.getName())
                .thenReturn(request.email());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        assertThrows(InvalidRoomTypeSelectionException.class,
                () -> reservationService.addReservation(request, authentication));

        verifyNoInteractions(roomRepository);
        verifyNoInteractions(reservationMapper);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldThrowRoomNotAvailableException() {

        User user = new User();

        Room room1 = new Room();
        Room room2 = new Room();

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 24),
                "Jan",
                "Nowak",
                "jan.nowak@gmail.com",
                "111222333",
                "Poland",
                3L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        3
                ))
        );

        when(authentication.getName())
                .thenReturn(request.email());
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));
        when(roomRepository.getAllRoomsByDatesHotelIdAndRoomTypeId(
                request.hotelId(),
                request.roomTypes().getFirst().roomTypeId(),
                request.checkInDate(),
                request.checkOutDate()
        ))
                .thenReturn(List.of(room1, room2));

        assertThrows(RoomNotAvailableException.class,
                () -> reservationService.addReservation(
                        request,
                        authentication
                ));

        verifyNoInteractions(reservationMapper);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldThrowUserNotFoundException() {

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 24),
                "Jan",
                "Nowak",
                "jan.nowak@gmail.com",
                "111222333",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        3
                ))
        );

        assertThrows(UserNotFoundException.class,
                () -> reservationService.addReservation(
                        request,
                        authentication
                ));

        verifyNoInteractions(roomRepository);
        verifyNoInteractions(reservationMapper);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldThrowInvalidRoomQuantityException() {
        User user = new User();

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 24),
                "Jan",
                "Nowak",
                "jan.nowak@gmail.com",
                "111222333",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        -5
                ))
        );

        when(authentication.getName())
                .thenReturn(request.email());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        assertThrows(InvalidRoomQuantityException.class,
                () -> reservationService.addReservation(
                        request,
                        authentication));

        verify(authentication).getName();
        verify(userRepository).findByEmail(request.email());
        verifyNoInteractions(reservationMapper);
        verifyNoInteractions(reservationRepository);

    }

}
