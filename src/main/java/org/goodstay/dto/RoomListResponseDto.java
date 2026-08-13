package org.goodstay.dto;

import java.math.BigDecimal;

public record RoomListResponseDto(
        Long id,
        BigDecimal pricePerNight,
        String roomType,
        Integer maxNumberOfGuests
) {}
