package org.goodstay.mapper;

import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.model.Room;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomMapper {

    public RoomListResponseDto toDto(Room room) {
        return new RoomListResponseDto(
                room.getId(),
                room.getPricePerNight(),
                room.getRoomType().getName(),
                room.getRoomType().getMaxGuests()
        );
    }

    public List<RoomListResponseDto> toDto(List<Room> rooms) {
        return rooms
                .stream()
                .map(this::toDto)
                .toList();
    }
}
