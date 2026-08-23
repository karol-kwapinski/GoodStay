package org.goodstay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ReservationInfoDto(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate checkInDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate checkOutDate,
        String cityName,
        String hotelName
) {}
