package org.goodstay.integrationTests;

import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.dto.AddHotelRequestDto;
import org.goodstay.dto.HotelBasicInfoDto;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelResponseDto;
import org.goodstay.exception.*;
import org.goodstay.model.*;
import org.goodstay.repository.HotelRepository;
import org.goodstay.service.HotelService;
import org.goodstay.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ApplicationConfiguration.class,
        TestDataFactory.class
})
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class HotelIntegrationTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    static Stream<Arguments> availableHotelsRequests() {

        return Stream.of(
                Arguments.of(
                        "Hotels without facilities",
                        false
                ),
                Arguments.of(
                        "Hotels with facilities",
                        true
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("availableHotelsRequests")
    void shouldReturnAvailableHotels(
            String description,
            boolean doesHotelhaveFacilities
    ) {

        User owner = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.HOTEL_OWNER
        );

        Hotel hotel1 = testDataFactory.createHotel(
                "Great hotel",
                5,
                "Good hotels",
                "Warsaw",
                "Adamowska",
                "28C",
                LocalTime.of(14, 0, 0),
                LocalTime.of(20, 0, 0),
                LocalTime.of(11, 0, 0),
                owner
        );

        Hotel hotel2 = testDataFactory.createHotel(
                "Big hotel",
                3,
                "Good hotels",
                "Warsaw",
                "Botomowska",
                "14A",
                LocalTime.of(12, 0, 0),
                LocalTime.of(22, 0, 0),
                LocalTime.of(11, 0, 0),
                owner
        );

        if (doesHotelhaveFacilities) {
            testDataFactory.createFacilityWithHotels(
                    "Hot tub",
                    List.of(hotel1, hotel2));
            testDataFactory.createFacilityWithHotels(
                    "Swimming pool",
                    List.of(hotel1));
            testDataFactory.createFacilityWithHotels(
                    "Fitness center",
                    List.of(hotel1));
            testDataFactory.createFacilityWithHotels(
                    "Restaurant",
                    List.of(hotel2));
        }

        RoomType roomType = testDataFactory.createRoomType("Family Suite", 3);

        testDataFactory.createRoom(
                roomType,
                hotel1,
                BigDecimal.valueOf(100.00)
        );

        testDataFactory.createRoom(
                roomType,
                hotel2,
                BigDecimal.valueOf(130.00)
        );

        HotelListRequestDto request = new HotelListRequestDto(
                "Warsaw",
                        LocalDate.now(),
                        LocalDate.now().plusDays(3),
                        List.of()
        );

        List<HotelResponseDto> expected = List.of(
                new HotelResponseDto(
                        hotel1.getId(),
                        hotel1.getName(),
                        hotel1.getCityName(),
                        hotel1.getStreet(),
                        hotel1.getBuildingNumber(),
                        hotel1.getStars(),
                        hotel1.getNumberOfRatings()
                ),
                new HotelResponseDto(
                        hotel2.getId(),
                        hotel2.getName(),
                        hotel2.getCityName(),
                        hotel2.getStreet(),
                        hotel2.getBuildingNumber(),
                        hotel2.getStars(),
                        hotel2.getNumberOfRatings()
                )
        );

        List<HotelResponseDto> response = hotelService.getAvailableHotels(request);

        assertEquals(expected, response);
    }

    @Test
    void shouldThrowInvalidDateRangeException() {
        HotelListRequestDto request = new HotelListRequestDto(
                "Warsaw",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(3),
                List.of()
        );

        assertThrows(InvalidDateRangeException.class,
                () -> hotelService.getAvailableHotels(request));
    }

    @Test
    void shouldReturnHotelWithGivenId() {
        User owner = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.HOTEL_OWNER
        );

        Hotel hotel = testDataFactory.createHotelWithData(owner);
        HotelResponseDto response = hotelService.getHotel(hotel.getId());

        HotelResponseDto expected = new HotelResponseDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCityName(),
                hotel.getStreet(),
                hotel.getBuildingNumber(),
                hotel.getStars(),
                hotel.getNumberOfRatings()
        );

        assertEquals(expected, response);
    }

    @Test
    void shouldThrowHotelDoesNotExistException() {
        assertThrows(HotelDoesNotExistException.class,
                () -> hotelService.getHotel(999L));
    }

    @Test
    void shouldAddHotel() {

        User owner = testDataFactory.createUser(
                "jan.nowak@gmail.com",
                UserRole.HOTEL_OWNER
        );

        AddHotelRequestDto request = new AddHotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(13, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                owner.getId()
        );

        HotelBasicInfoDto response = hotelService.addHotel(request);
        List<Hotel> hotels = hotelRepository.findHotelsByOwnerId(owner.getId());
        Hotel hotel = hotels.getFirst();

        assertNotNull(hotel.getId());
        assertEquals(hotel.getName(), request.name());
        assertEquals(hotel.getCityName(), request.cityName());
        assertEquals(hotel.getStreet(), request.street());
        assertEquals(hotel.getBuildingNumber(), request.buildingNumber());
        assertEquals(hotel.getStars(), request.stars());
        assertEquals(hotel.getCheckInFrom(), request.checkInFrom());
        assertEquals(hotel.getCheckInUntil(), request.checkInUntil());
        assertEquals(hotel.getCheckOutUntil(), request.checkOutUntil());
        assertEquals(hotel.getBrand(), request.brand());
        assertEquals(hotel.getOwner().getId(), request.ownerId());

        assertEquals(hotel.getId(), response.id());
        assertEquals(request.name(), response.name());
        assertEquals(request.cityName(), response.cityName());
    }

    @Test
    void shouldThrowUserNotFoundException() {
        AddHotelRequestDto request = new AddHotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(13, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                999L
        );

        assertThrows(UserNotFoundException.class,
                () -> hotelService.addHotel(request));
    }

    @Test
    void shouldThrowInvalidTimeRangeException() {
        User owner = testDataFactory.createUser(
                "jan.nowak@gmail.com",
                UserRole.HOTEL_OWNER
        );

        AddHotelRequestDto request = new AddHotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(12, 0, 0),
                LocalTime.of(11, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                owner.getId()
        );

        assertThrows(InvalidTimeRangeException.class,
                () -> hotelService.addHotel(request));
    }

    @Test
    void shouldThrowHotelWithSameLocationDataAlreadyExistsException() {
        User owner = testDataFactory.createUser(
                "jan.nowak@gmail.com",
                UserRole.HOTEL_OWNER
        );

        testDataFactory.createHotel(
                "Great hotel",
                3,
                "Best hotels",
                "Cracow",
                "Mickiewicza",
                "10C",
                LocalTime.of(14, 0, 0),
                LocalTime.of(20, 0, 0),
                LocalTime.of(10, 0, 0),
                owner
        );

        AddHotelRequestDto request = new AddHotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                owner.getId()
        );

        assertThrows(HotelWithSameLocationDataAlreadyExistsException.class,
                () -> hotelService.addHotel(request));
    }

}
