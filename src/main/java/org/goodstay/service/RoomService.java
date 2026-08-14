package org.goodstay.service;

import org.goodstay.dto.RoomListRequestDto;
import org.goodstay.dto.RoomListResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    List<RoomListResponseDto> getAllRoomsByDatesAndHotelId(
            Long hotelId,
            RoomListRequestDto request
    );
}
