package org.goodstay.mapper;

import org.goodstay.dto.HotelListResponseDto;
import org.goodstay.model.Hotel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    public HotelListResponseDto toDto(Hotel hotel) {
        return new HotelListResponseDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCityName(),
                hotel.getStreet(),
                hotel.getBuildingNumber(),
                hotel.getStars(),
                hotel.getNumberOfRatings()
        );
    }

    public List<HotelListResponseDto> toDto(List<Hotel> hotelList) {
        return hotelList.stream()
                .map(this::toDto)
                .toList();
    }
}
