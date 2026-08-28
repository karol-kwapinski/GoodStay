package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelResponseDto;
import org.goodstay.exception.HotelDoesNotExistException;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.mapper.HotelMapper;
import org.goodstay.model.Hotel;
import org.goodstay.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

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
}
