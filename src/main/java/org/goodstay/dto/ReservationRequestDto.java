package org.goodstay.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ReservationRequestDto(

        @NotNull
        @FutureOrPresent
        LocalDate checkInDate,

        @NotNull
        @Future
        LocalDate checkOutDate,

        @NotNull
        @Size(min = 2, max = 60)
        String firstName,

        @NotNull
        @Size(min = 2, max = 60)
        String lastName,

        @NotNull
        String email,

        @NotNull
        String phoneNumber,

        @NotNull
        String country,

        @NotNull
        Long hotelId,

        @NotNull
        List<RoomTypeSelectionDto> roomTypes

) {}
