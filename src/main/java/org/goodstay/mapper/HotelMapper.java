package org.goodstay.mapper;

import org.goodstay.dto.*;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    public HotelResponseDto toHotelResponseDto(Hotel hotel) {
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

    public HotelResponseFullDataDto toHotelResponseFullDataDto(Hotel hotel) {
        return new HotelResponseFullDataDto(
                hotel.getName(),
                hotel.getCityName(),
                hotel.getStreet(),
                hotel.getBuildingNumber(),
                hotel.getStars(),
                hotel.getCheckInFrom(),
                hotel.getCheckInUntil(),
                hotel.getCheckOutUntil(),
                hotel.getBrand(),
                hotel.getOwner().getId()
        );
    }

    public List<HotelResponseDto> toHotelResponseDto(List<Hotel> hotelList) {
        return hotelList.stream()
                .map(this::toHotelResponseDto)
                .toList();
    }

    public Hotel toEntity(HotelRequestDto request, User owner) {
        Hotel hotel = new Hotel();
        return toEntity(request, owner, hotel);
    }

    public Hotel toEntity(HotelRequestDto request, User owner, Hotel hotel) {
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

    public PageResponse<HotelBasicInfoDto> toPage(Page<Hotel> page) {

        List<HotelBasicInfoDto> content = page.getContent().stream()
                .map(hotel -> new HotelBasicInfoDto(
                        hotel.getId(),
                        hotel.getName(),
                        hotel.getCityName()))
                .toList();

        return new PageResponse<>(
                content,
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
