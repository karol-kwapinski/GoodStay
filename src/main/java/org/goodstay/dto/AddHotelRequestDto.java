package org.goodstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

import java.time.LocalTime;

public record AddHotelRequestDto(

        @NotNull
        @NotBlank
        @Size(min = 1, max = 100)
        String name,

        @NotNull
        @NotBlank
        @Size(min = 2, max = 100)
        String cityName,

        @NotNull
        @NotBlank
        @Size(min = 2, max = 100)
        String street,

        @NotNull
        @NotBlank
        @Size(min = 1, max = 6)
        String buildingNumber,

        @NotNull
        @Range(min = 1, max = 5)
        Integer stars,

        @NotNull
        LocalTime checkInFrom,

        @NotNull
        LocalTime checkInUntil,

        @NotNull
        LocalTime checkOutUntil,

        @NotNull
        @NotBlank
        @Size(min = 1, max = 100)
        String brand,

        @NotNull
        Long ownerId
) {}
