package org.goodstay.dto;

public record HotelListResponseDto(
        Long id,
        String name,
        String cityName,
        String street,
        String buildingNumber,
        Integer stars,
        Integer numberOfRatings
) {}
