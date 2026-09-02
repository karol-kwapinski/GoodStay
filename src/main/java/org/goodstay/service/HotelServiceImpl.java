package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.*;
import org.goodstay.exception.*;
import org.goodstay.mapper.HotelMapper;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
import org.goodstay.repository.HotelRepository;
import org.goodstay.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final UserRepository userRepository;

    private User prepareForEditOrAddHotel(HotelRequestDto request) {

        User owner = userRepository.findById(request.ownerId()).orElseThrow(
                UserNotFoundException::new
        );

        if (request.checkInUntil().isBefore(request.checkInFrom())
                || request.checkOutUntil().isAfter(request.checkInFrom())) {
            throw new InvalidTimeRangeException();
        }

        return owner;
    }

    public List<HotelResponseDto> getAvailableHotels(HotelListRequestDto request) {

        if (request.checkOutDate().isBefore(request.checkInDate())
                || request.checkOutDate().isEqual(request.checkInDate())) {
            throw new InvalidDateRangeException();
        }

        if (request.facilities() == null || request.facilities().isEmpty()) {
            List<Hotel> hotelList = hotelRepository.getAvailableHotels(
                    request.cityName(),
                    request.checkInDate(),
                    request.checkOutDate()
            );
            return hotelMapper.toHotelResponseDto(hotelList);
        }

        List<Hotel> hotelList = hotelRepository.getAvailableHotelsWithFacilities(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate(),
                request.facilities(),
                request.facilities().size()
        );
        return hotelMapper.toHotelResponseDto(hotelList);
    }

    public HotelResponseDto getHotel(Long hotelId) {

       Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
               HotelDoesNotExistException::new
       );

       return hotelMapper.toHotelResponseDto(hotel);
    }

    public HotelResponseFullDataDto getHotelWithFullData(Long hotelId) {

        Hotel hotel = hotelRepository.findByIdAndFetchOwner(hotelId).orElseThrow(
                HotelDoesNotExistException::new
        );

        return hotelMapper.toHotelResponseFullDataDto(hotel);
    }

    @Transactional
    public HotelBasicInfoDto addHotel(HotelRequestDto request) {

        User owner = prepareForEditOrAddHotel(request);

        if (hotelRepository.existsHotelsByCityNameAndStreetAndBuildingNumber(
                request.cityName(),
                request.street(),
                request.buildingNumber()
        )) {
            throw new HotelWithSameLocationDataAlreadyExistsException();
        }

        Hotel hotel = hotelMapper.toEntity(request, owner);
        Hotel saved = hotelRepository.save(hotel);

        return hotelMapper.toHotelBasicInfoDto(saved);
    }

    @Transactional
    public HotelBasicInfoDto editHotel(Long hotelId, HotelRequestDto request) {

        Hotel hotelToEdit = hotelRepository.findById(hotelId).orElseThrow(
                HotelDoesNotExistException::new
        );
        User owner = prepareForEditOrAddHotel(request);
        Hotel hotel = hotelMapper.toEntity(request, owner, hotelToEdit);

        return hotelMapper.toHotelBasicInfoDto(hotel);
    }

    public PageResponse<HotelBasicInfoDto> getAllHotels(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> pageWithHotels = hotelRepository.findAllByOrderByIdAsc(pageable);
        return hotelMapper.toPage(pageWithHotels);
    }


}
