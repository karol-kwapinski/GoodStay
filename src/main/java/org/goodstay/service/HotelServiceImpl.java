package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.AddHotelRequestDto;
import org.goodstay.dto.HotelBasicInfoDto;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelResponseDto;
import org.goodstay.exception.*;
import org.goodstay.mapper.HotelMapper;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
import org.goodstay.repository.HotelRepository;
import org.goodstay.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final UserRepository userRepository;

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
            return hotelMapper.toDto(hotelList);
        }

        List<Hotel> hotelList = hotelRepository.getAvailableHotelsWithFacilities(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate(),
                request.facilities(),
                request.facilities().size()
        );
        return hotelMapper.toDto(hotelList);
    }

    public HotelResponseDto getHotel(Long hotelId) {

       Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(
               HotelDoesNotExistException::new
       );

       return hotelMapper.toDto(hotel);
    }

    @Transactional
    public HotelBasicInfoDto addHotel(AddHotelRequestDto request) {

        User owner = userRepository.findById(request.ownerId()).orElseThrow(
                UserNotFoundException::new
        );

        if (request.checkInUntil().isBefore(request.checkInFrom())
            || request.checkOutUntil().isAfter(request.checkInFrom())) {
            throw new InvalidTimeRangeException();
        }

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
}
