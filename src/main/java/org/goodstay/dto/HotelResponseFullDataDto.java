package org.goodstay.dto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record HotelResponseFullDataDto(
        String name,
        String cityName,
        String street,
        String buildingNumber,
        Integer stars,

        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInFrom,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInUntil,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutUntil,

        String brand,
        Long ownerId
) {}
