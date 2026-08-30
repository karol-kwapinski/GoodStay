package org.goodstay.mapper;

import org.goodstay.dto.AddHotelRequestDto;
import org.goodstay.dto.HotelBasicInfoDto;
import org.goodstay.dto.HotelResponseDto;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
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

    public Hotel toEntity(AddHotelRequestDto request, User owner) {
        Hotel hotel = new Hotel();
        hotel.setName(request.name());
        hotel.setCityName(request.cityName());
        hotel.setStreet(request.street());
        hotel.setBuildingNumber(request.buildingNumber());
        hotel.setStars(request.stars());
        hotel.setCheckInFrom(request.checkInFrom());
        hotel.setCheckInUntil(request.checkInUntil());
        hotel.setCheckOutUntil(request.checkOutUntil());
        hotel.setBrand(request.brand());
        hotel.setOwner(owner);

        return hotel;
    }

    public HotelBasicInfoDto toHotelBasicInfoDto(Hotel hotel) {
        return new HotelBasicInfoDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCityName()
        );
    }
}
