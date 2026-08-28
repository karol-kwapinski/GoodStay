package org.goodstay.mapper;

import org.goodstay.dto.HotelResponseDto;
import org.goodstay.model.Hotel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    public HotelResponseDto toDto(Hotel hotel) {
        return new HotelResponseDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCityName(),
                hotel.getStreet(),
                hotel.getBuildingNumber(),
                hotel.getStars(),
                hotel.getNumberOfRatings()
        );
    }

    public List<HotelResponseDto> toDto(List<Hotel> hotelList) {
        return hotelList.stream()
                .map(this::toDto)
                .toList();
    }
}
