package org.goodstay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ReservationDetailsDto(
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInFrom,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkInUntil,
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOutUntil,
        String guestFirstName,
        String guestLastName,
        String guestEmail ,
        String guestPhoneNumber,
        String guestCountry,
        BigDecimal totalPrice,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String reservationStatus,
        List<RoomTypeAndMaxGuestsDto> roomsDetailsList
) {}
