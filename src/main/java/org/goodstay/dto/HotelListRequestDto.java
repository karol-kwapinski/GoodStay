package org.goodstay.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record HotelListRequestDto(

        @NotBlank
        String cityName,

        @FutureOrPresent
        LocalDate checkInDate,

        @Future
        LocalDate checkOutDate,

        List<String> facilities
) {
        public HotelListRequestDto {
                facilities = facilities == null ? List.of() : facilities;
        }
}
