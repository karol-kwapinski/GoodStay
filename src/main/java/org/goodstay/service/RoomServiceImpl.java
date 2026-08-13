package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.RoomListRequestDto;
import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.exception.InvalidDateRangeException;
import org.goodstay.mapper.RoomMapper;
import org.goodstay.model.Room;
import org.goodstay.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public List<RoomListResponseDto> getAllRoomsByDatesAndHotelId(
            Long hotelId,
            RoomListRequestDto request) {

        if (request.checkOutDate().isBefore(request.checkInDate())) {
            throw new InvalidDateRangeException();
        }

        List<Room> rooms = roomRepository.getAllRoomsByDatesAndHotelId(
                hotelId,
                request.checkInDate(),
                request.checkOutDate()
        );

        return roomMapper.toDto(rooms);
    }
}
