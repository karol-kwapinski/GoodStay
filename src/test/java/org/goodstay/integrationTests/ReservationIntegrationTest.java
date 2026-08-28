package org.goodstay.integrationTests;

import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.dto.*;
import org.goodstay.exception.*;
import org.goodstay.model.*;
import org.goodstay.repository.*;
import org.goodstay.service.ReservationService;
import org.goodstay.service.UserService;
import org.goodstay.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = {
        ApplicationConfiguration.class,
        TestDataFactory.class
})
@Transactional
public class ReservationIntegrationTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    static Stream<Arguments> getInvalidRequests() {
        return Stream.of(
                Arguments.of(
                        "Check out date is before check in date",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 18),
                                LocalDate.of(2026, 8, 16),
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
                        )
                ),
                Arguments.of(
                        "Check out date is the same as check in date",
                        new ReservationRequestDto(
                                LocalDate.of(2026, 8, 18),
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
                        )
                )
        );
    }

    @Test
    void shouldAddReservation() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.nowak@gmail.com",
                null
        );

        User owner = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.HOTEL_OWNER);
        User user = testDataFactory.createUser("jan.nowak@gmail.com", UserRole.USER);
        Hotel hotel = testDataFactory.createHotel(
                "Warsaw Hotel",
                5,
                "Good hotels",
                "Warsaw",
                "Mickiewicza",
                "24B",
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(14, 30, 0),
                owner);
        RoomType roomType = testDataFactory.createRoomType("Bedroom", 3);
        Room room1 = testDataFactory.createRoom(roomType, hotel, BigDecimal.valueOf(100.00));
        Room room2 = testDataFactory.createRoom(roomType, hotel, BigDecimal.valueOf(130.00));

        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 16),
                LocalDate.of(2026, 8, 18),
                "Jan",
                "Kowalski",
                "jan.kowalski@gmail.com",
                "333444555",
                "Poland",
                hotel.getId(),
                List.of(new RoomTypeSelectionDto(
                        roomType.getId(),
                        2
                ))
        );

        reservationService.addReservation(request, authentication);

        Reservation reservation = reservationRepository.getReservationById(1L).orElseThrow();

        assertEquals(request.checkInDate(), reservation.getCheckInDate());
        assertEquals(request.checkOutDate(), reservation.getCheckOutDate());
        assertEquals(request.firstName(), reservation.getGuestFirstName());
        assertEquals(request.lastName(), reservation.getGuestLastName());
        assertEquals(request.email(), reservation.getGuestEmail());
        assertEquals(request.phoneNumber(), reservation.getGuestPhoneNumber());
        assertEquals(request.country(), reservation.getGuestCountry());
        assertEquals(List.of(room1, room2), reservation.getRooms());
        assertEquals(ReservationStatus.PAID, reservation.getStatus());
        assertEquals(BigDecimal.valueOf(460.00), reservation.getTotalPrice());
        assertEquals(user, reservation.getUser());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getInvalidRequests")
    void shouldThrowInvalidDateRangeException(String description, ReservationRequestDto request) {

        User user = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        assertThrows(InvalidDateRangeException.class,
                () -> reservationService.addReservation(request, authentication));
    }

    @Test
    void shouldThrowUserNotFoundException() {
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 18),
                LocalDate.of(2026, 8, 20),
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                null
        );

        assertThrows(UserNotFoundException.class,
                () -> reservationService.addReservation(request, authentication));
    }

    @Test
    void shouldThrowInvalidRoomTypeSelectionException() {
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 18),
                LocalDate.of(2026, 8, 21),
                "Jan",
                "Kowalski",
                "jan.kowalski@gmail.com",
                "333444555",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        2
                        ),
                        new RoomTypeSelectionDto(
                                1L,
                                2
                        ))
        );

        User user = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        assertThrows(InvalidRoomTypeSelectionException.class,
                () -> reservationService.addReservation(request, authentication));
    }

    @Test
    void shouldThrowRoomNotAvailableException() {
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 18),
                LocalDate.of(2026, 8, 21),
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

        User owner = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.HOTEL_OWNER);
        User user1 = testDataFactory.createUser("agata.nowak@interia.pl", UserRole.USER);
        RoomType roomType = testDataFactory.createRoomType("Bedroom", 3);
        Hotel hotel = testDataFactory.createHotel(
                "Cracow Hotel",
                3,
                "Good hotels",
                "Cracow",
                "Mickiewicza",
                "24B",
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(14, 30, 0),
                owner);
        Room room1 = testDataFactory.createRoom(roomType, hotel, BigDecimal.valueOf(120.00));
        Room room2 = testDataFactory.createRoom(roomType, hotel, BigDecimal.valueOf(180.00));
        testDataFactory.createReservation(
                List.of(room1, room2),
                user1,
                LocalDate.of(2026, 8, 17),
                LocalDate.of(2026, 8, 20),
                "agata.nowak@interia.pl",
                "Agata",
                "Nowak",
                "Poland",
                "123456456",
                ReservationStatus.PAID);

        User user2 = testDataFactory.createUser("daniel.kowal@gmail.com", UserRole.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user2.getEmail(),
                null
        );

        assertThrows(RoomNotAvailableException.class,
                () -> reservationService.addReservation(request, authentication));
    }

    @Test
    void shouldThrowInvalidRoomQuantityException() {
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDate.of(2026, 8, 18),
                LocalDate.of(2026, 8, 21),
                "Jan",
                "Kowalski",
                "jan.kowalski@gmail.com",
                "333444555",
                "Poland",
                1L,
                List.of(new RoomTypeSelectionDto(
                        1L,
                        -3
                ))
        );

        User user = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        assertThrows(InvalidRoomQuantityException.class,
                () -> reservationService.addReservation(request, authentication));
    }

    @Test
    void shouldReturnReservations() {
        User owner = testDataFactory.createUser("Agata.kowal@interia.pl", UserRole.HOTEL_OWNER);
        User user = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        Hotel hotel = testDataFactory.createHotel(
                "Cracow Hotel",
                3,
                "Good hotels",
                "Cracow",
                "Mickiewicza",
                "24B",
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(14, 30, 0),
                owner);
        RoomType roomType = testDataFactory.createRoomType("Bedroom", 3);
        Room room = testDataFactory.createRoom(roomType, hotel, BigDecimal.valueOf(120.00));

        Reservation reservation1 = testDataFactory.createReservation(
                List.of(room),
                user,
                LocalDate.of(2026, 8, 17),
                LocalDate.of(2026, 8, 17),
                "jan.kowalski@gmail.com",
                "Jan",
                "Kowalski",
                "Poland",
                "444555666",
                ReservationStatus.PAID);

        Reservation reservation2 = testDataFactory.createReservation(
                List.of(room),
                user,
                LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 6, 20),
                "jan.kowalski@gmail.com",
                "Jan",
                "Kowalski",
                "Poland",
                "444555666",
                ReservationStatus.PAID);

        ReservationInfoDto reservation1Info = new ReservationInfoDto(
                reservation1.getId(),
                LocalDate.of(2026, 8, 17),
                LocalDate.of(2026, 8, 17),
                hotel.getCityName(),
                hotel.getName()
        );

        ReservationInfoDto reservation2Info = new ReservationInfoDto(
                reservation2.getId(),
                LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 6, 20),
                hotel.getCityName(),
                hotel.getName()
        );

        List<ReservationInfoDto> reservations =
                reservationService.getReservationInfo(authentication);

        assertEquals(2, reservations.size());
        assertEquals(List.of(reservation1Info, reservation2Info), reservations);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenFetchingReservations() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                null
        );

        assertThrows(UserNotFoundException.class,
                () -> reservationService.getReservationInfo(authentication));
    }

    @Test
    void shouldReturnReservationDetails() {

        User owner = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.HOTEL_OWNER);
        User user = testDataFactory.createUser("jan.nowak@gmail.com", UserRole.USER);

        Hotel hotel = testDataFactory.createHotel(
                "Warsaw Hotel",
                5,
                "Good hotels",
                "Warsaw",
                "Mickiewicza",
                "24B",
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(14, 30, 0),
                owner);

        RoomType roomType1 = testDataFactory.createRoomType("Bedroom", 3);
        RoomType roomType2 = testDataFactory.createRoomType("Apartment", 4);

        Room room1 = testDataFactory.createRoom(roomType1, hotel, BigDecimal.valueOf(120.00));
        Room room2 = testDataFactory.createRoom(roomType1, hotel, BigDecimal.valueOf(150.00));
        Room room3 = testDataFactory.createRoom(roomType2, hotel, BigDecimal.valueOf(200.00));

        Reservation reservation = testDataFactory.createReservation(
                List.of(room1, room2, room3),
                user,
                LocalDate.of(2026, 8, 17),
                LocalDate.of(2026, 8, 27),
                "jan.nowak@gmail.com",
                "Jan",
                "Nowak",
                "Poland",
                "333444555",
                ReservationStatus.PAID
        );

        RoomTypeAndMaxGuestsDto roomTypeAndMaxGuestsDto1 = new RoomTypeAndMaxGuestsDto(
                roomType1.getName(),
                2,
                roomType1.getMaxGuests()
        );
        RoomTypeAndMaxGuestsDto roomTypeAndMaxGuestsDto2 = new RoomTypeAndMaxGuestsDto(
                roomType2.getName(),
                1,
                roomType2.getMaxGuests()
        );

        ReservationDetailsDto reservationDetailsDto = new ReservationDetailsDto(
                hotel.getCheckInFrom(),
                hotel.getCheckInUntil(),
                hotel.getCheckOutUntil(),
                reservation.getGuestFirstName(),
                reservation.getGuestLastName(),
                reservation.getGuestEmail(),
                reservation.getGuestPhoneNumber(),
                reservation.getGuestCountry(),
                reservation.getTotalPrice(),
                reservation.getCreatedAt(),
                reservation.getStatus().name(),
                List.of(roomTypeAndMaxGuestsDto1, roomTypeAndMaxGuestsDto2)
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        ReservationDetailsDto result = reservationService
                .getReservationDetails(authentication, reservation.getId());

        assertEquals(reservationDetailsDto, result);
    }
}
