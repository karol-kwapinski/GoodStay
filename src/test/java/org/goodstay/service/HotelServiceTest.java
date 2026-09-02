package org.goodstay.service;

import org.goodstay.dto.*;
import org.goodstay.exception.*;
import org.goodstay.mapper.HotelMapper;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
import org.goodstay.repository.HotelRepository;
import org.goodstay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private HotelRequestDto createValidHotelRequestDto(User owner) {
        return new HotelRequestDto(
                "Warsaw hotel",
                "Warsaw",
                "Mickiewicza",
                "42C",
                4,
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(11, 0, 0),
                "Good hotels",
                owner.getId()
        );
    }

    static Stream<Arguments> invalidTimeRangeAddHotelRequests() {
        return Stream.of(
                Arguments.of(
                    "Check in from time is after check in until time",
                        new HotelRequestDto(
                                "Warsaw hotel",
                                "Warsaw",
                                "Mickiewicza",
                                "42C",
                                4,
                                LocalTime.of(11, 0, 0),
                                LocalTime.of(10, 59, 59),
                                LocalTime.of(9, 0, 0),
                                "Good hotels",
                                1L
                        )
                ),
                Arguments.of(
                        "Check out time is before check in from time",
                        new HotelRequestDto(
                                "Warsaw hotel",
                                "Warsaw",
                                "Mickiewicza",
                                "42C",
                                4,
                                LocalTime.of(11, 0, 0),
                                LocalTime.of(21, 0, 0),
                                LocalTime.of(12, 0, 0),
                                "Good hotels",
                                1L
                        )
                )
        );
    }

    @Test
    void shouldReturnAllAvailableHotels() {

        Hotel hotel = new Hotel();

        List<Hotel> listOfHotels = List.of(hotel);

        HotelListRequestDto request = new HotelListRequestDto(
                "Warsaw",
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 30),
                List.of()
        );

        HotelResponseDto response = new HotelResponseDto(
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

        when(hotelMapper.toHotelResponseDto(listOfHotels))
                .thenReturn(List.of(response));

        List<HotelResponseDto> hotelList = hotelService.getAvailableHotels(request);

        assertEquals(List.of(response), hotelList);
        assertEquals(1, hotelList.size());
        assertSame(response, hotelList.getFirst());

        verify(hotelRepository).getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate());

        verify(hotelMapper).toHotelResponseDto(listOfHotels);
    }

    @Test
    void shouldThrowInvalidDateRangeException() {

        HotelListRequestDto request = new HotelListRequestDto(
                "Cracow",
                LocalDate.of(2026, 8, 30),
                LocalDate.of(2026, 8, 15),
                List.of("Parking")
        );

        assertThrows(InvalidDateRangeException.class,
                () -> hotelService.getAvailableHotels(request));

        verify(hotelRepository, never()).getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate()
        );

        verify(hotelMapper, never()).toHotelResponseDto(anyList());
    }

    @Test
    void shouldReturnHotelWithGivenHotelId() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        HotelResponseDto expected = new HotelResponseDto(
                1L,
                "Great hotel",
                "Warsaw",
                "Mickiewicza",
                "18B",
                5,
                0
        );

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        when(hotelMapper.toHotelResponseDto(hotel))
                .thenReturn(expected);

        HotelResponseDto response = hotelService.getHotel(1L);

        assertEquals(expected, response);

        verify(hotelRepository).findById(1L);
        verify(hotelMapper).toHotelResponseDto(hotel);
    }

    @Test
    void shouldThrowHotelDoesNotExistExceptionWhenFetchingHotel() {
        assertThrows(HotelDoesNotExistException.class,
                () -> hotelService.getHotel(1L));

        verify(hotelRepository).findById(1L);
        verifyNoInteractions(hotelMapper);
    }

    @Test
    void shouldCreateHotel() {

        User owner = new User();
        owner.setId(1L);

        Hotel hotel = new Hotel();

        HotelRequestDto request = createValidHotelRequestDto(owner);

        HotelBasicInfoDto expected = new HotelBasicInfoDto(
                1L,
                request.name(),
                request.cityName()
        );

        when(userRepository.findById(request.ownerId()))
                .thenReturn(Optional.of(owner));

        when(hotelMapper.toEntity(request, owner))
                .thenReturn(hotel);

        when(hotelRepository.save(hotel))
                .thenReturn(hotel);

        when(hotelMapper.toHotelBasicInfoDto(hotel))
                .thenReturn(expected);

        HotelBasicInfoDto response = hotelService.addHotel(request);

        assertEquals(expected, response);

        verify(userRepository).findById(request.ownerId());
        verify(hotelMapper).toEntity(request, owner);
        verify(hotelRepository).save(hotel);
        verify(hotelMapper).toHotelBasicInfoDto(hotel);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenAddingHotel() {

        User owner = new User();
        owner.setId(1L);

        HotelRequestDto request = createValidHotelRequestDto(owner);

        assertThrows(UserNotFoundException.class,
                () -> hotelService.addHotel(request));

        verify(userRepository).findById(request.ownerId());
        verifyNoInteractions(hotelMapper);
        verifyNoInteractions(hotelRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTimeRangeAddHotelRequests")
    void shouldThrowInvalidTimeRangeException(
            String description,
            HotelRequestDto request
    ) {
        User user = new User();

        when(userRepository.findById(request.ownerId()))
                .thenReturn(Optional.of(user));

        assertThrows(InvalidTimeRangeException.class,
                () -> hotelService.addHotel(request));

        verify(userRepository).findById(request.ownerId());
        verifyNoInteractions(hotelMapper);
        verifyNoInteractions(hotelRepository);
    }

    @Test
    void shouldAddHotel() {

        User owner = new User();
        owner.setId(1L);

        Hotel hotel = new Hotel();

        HotelRequestDto request = new HotelRequestDto(
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

        HotelBasicInfoDto expected = new HotelBasicInfoDto(
                1L,
                "Good hotel",
                "Cracow"
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(hotelRepository.existsHotelsByCityNameAndStreetAndBuildingNumber(
                request.cityName(),
                request.street(),
                request.buildingNumber()
        ))
                .thenReturn(false);

        when(hotelMapper.toEntity(request, owner))
                .thenReturn(hotel);

        when(hotelRepository.save(hotel))
                .thenReturn(hotel);

        when(hotelMapper.toHotelBasicInfoDto(hotel))
                .thenReturn(expected);

        HotelBasicInfoDto response = hotelService.addHotel(request);
        assertEquals(expected, response);

        verify(userRepository).findById(owner.getId());
        verify(hotelRepository).existsHotelsByCityNameAndStreetAndBuildingNumber(
                request.cityName(),
                request.street(),
                request.buildingNumber()
        );
        verify(hotelMapper).toEntity(request, owner);
        verify(hotelRepository).save(hotel);
        verify(hotelMapper).toHotelBasicInfoDto(hotel);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        HotelRequestDto request = new HotelRequestDto(
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

        verify(userRepository).findById(999L);
        verifyNoInteractions(hotelRepository);
        verifyNoInteractions(hotelMapper);
    }

    @Test
    void shouldThrowInvalidTimeRangeException() {
        User user = new User();

        HotelRequestDto request = new HotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(12, 0, 0),
                LocalTime.of(11, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                1L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThrows(InvalidTimeRangeException.class,
                () -> hotelService.addHotel(request));

        verify(userRepository).findById(1L);
        verifyNoInteractions(hotelRepository);
        verifyNoInteractions(hotelMapper);
    }

    @Test
    void shouldThrowHotelWithSameLocationDataAlreadyExistsException() {
        User user = new User();

        HotelRequestDto request = new HotelRequestDto(
                "Great hotel",
                "Cracow",
                "Mickiewicza",
                "10C",
                4,
                LocalTime.of(14, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(10, 0, 0),
                "Good hotels",
                1L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(hotelRepository.existsHotelsByCityNameAndStreetAndBuildingNumber(
                request.cityName(),
                request.street(),
                request.buildingNumber()
        ))
                .thenReturn(true);

        assertThrows(HotelWithSameLocationDataAlreadyExistsException.class,
                () -> hotelService.addHotel(request));

        verify(userRepository).findById(1L);
        verify(hotelRepository).existsHotelsByCityNameAndStreetAndBuildingNumber(
                request.cityName(),
                request.street(),
                request.buildingNumber()
        );
        verifyNoInteractions(hotelMapper);
    }

    @Test
    void shouldReturnHotelsWithGivenPageNumberAndSize() {

        Hotel hotel1 = new Hotel();
        Hotel hotel2 = new Hotel();
        Hotel hotel3 = new Hotel();

        List<Hotel> hotels = List.of(hotel1, hotel2, hotel3);
        Page<Hotel> expectedPage = new PageImpl<>(hotels);

        HotelBasicInfoDto hotel1Info = new HotelBasicInfoDto(
                1L,
                "Great hotel",
                "Warsaw"
        );

        HotelBasicInfoDto hotel2Info = new HotelBasicInfoDto(
                2L,
                "Good hotel",
                "Cracow"
        );

        HotelBasicInfoDto hotel3Info = new HotelBasicInfoDto(
                3L,
                "Just hotel",
                "Poznan"
        );

        PageResponse<HotelBasicInfoDto> expectedResponse = new PageResponse<>(
                List.of(hotel1Info, hotel2Info, hotel3Info),
                10,
                0,
                3,
                1
        );

        when(hotelRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(expectedPage);

        when(hotelMapper.toPage(expectedPage))
                .thenReturn(expectedResponse);

        PageResponse<HotelBasicInfoDto> response = hotelService.getAllHotels(0, 10);

        assertEquals(expectedResponse, response);

        verify(hotelRepository).findAll(PageRequest.of(0, 10));
        verify(hotelMapper).toPage(expectedPage);
    }

    @Test
    void shouldEditHotel() {
        User owner = new User();
        owner.setId(1L);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        HotelRequestDto request = createValidHotelRequestDto(owner);

        Hotel hotelAfterEdit = new Hotel();
        hotel.setId(hotel.getId());
        hotel.setName(request.name());
        hotel.setStreet(request.cityName());

        HotelBasicInfoDto expected = new HotelBasicInfoDto(
                hotelAfterEdit.getId(),
                hotelAfterEdit.getName(),
                hotelAfterEdit.getCityName()
        );

        when(hotelRepository.findById(hotel.getId()))
                .thenReturn(Optional.of(hotel));

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(hotelMapper.toEntity(request, owner, hotel))
                .thenReturn(hotelAfterEdit);

        when(hotelMapper.toHotelBasicInfoDto(hotelAfterEdit))
                .thenReturn(expected);

        HotelBasicInfoDto response = hotelService.editHotel(hotel.getId(), request);
        assertEquals(expected, response);

        verify(hotelRepository).findById(owner.getId());
        verify(userRepository).findById(owner.getId());
        verify(hotelMapper).toEntity(request, owner, hotel);
        verify(hotelMapper).toHotelBasicInfoDto(hotelAfterEdit);
    }

    @Test
    void shouldThrowHotelDoesNotExistException() {
        User owner = new User();
        owner.setId(1L);

        HotelRequestDto request = createValidHotelRequestDto(owner);

        assertThrows(HotelDoesNotExistException.class,
                () -> hotelService.editHotel(1L, request));

        verify(hotelRepository).findById(1L);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(hotelMapper);
    }

    @Test
    void shouldReturnHotelWithFullData() {
        Hotel hotel = new Hotel();

        HotelResponseFullDataDto expected = new HotelResponseFullDataDto(
                "Great hotel",
                "Warsaw",
                "Bokiewicza",
                "67",
                3,
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(11, 0, 0),
                "Good hotels",
                1L
        );

        when(hotelRepository.findByIdAndFetchOwner(1L))
                .thenReturn(Optional.of(hotel));

        when(hotelMapper.toHotelResponseFullDataDto(hotel))
                .thenReturn(expected);

        HotelResponseFullDataDto response = hotelService.getHotelWithFullData(1L);
        assertEquals(expected, response);

        verify(hotelRepository).findByIdAndFetchOwner(1L);
        verify(hotelMapper).toHotelResponseFullDataDto(hotel);
    }

    @Test
    void shouldThrowHotelDoesNotExistExceptionWhenFetchingHotelWithFullData() {
        assertThrows(HotelDoesNotExistException.class,
                () -> hotelService.getHotelWithFullData(1L));

        verify(hotelRepository).findByIdAndFetchOwner(1L);
        verifyNoInteractions(hotelMapper);
    }

 }
