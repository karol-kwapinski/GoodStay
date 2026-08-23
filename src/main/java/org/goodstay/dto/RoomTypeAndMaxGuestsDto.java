package org.goodstay.dto;

public record RoomTypeAndMaxGuestsDto(
        String roomType,
        Integer quantity,
        Integer maxGuests
) {}
