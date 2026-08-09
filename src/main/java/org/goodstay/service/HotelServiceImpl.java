package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelListResponseDto;
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

    public List<HotelListResponseDto> getAvailableHotels(HotelListRequestDto request) {

        if(request.checkOutDate().isBefore(request.checkInDate())) {
            throw new InvalidDateRangeException();
        }

        List<Hotel> hotelList = hotelRepository.getAvailableHotels(
                request.cityName(),
                request.checkInDate(),
                request.checkOutDate()
        );

        return hotelMapper.toDto(hotelList);
    }
}
