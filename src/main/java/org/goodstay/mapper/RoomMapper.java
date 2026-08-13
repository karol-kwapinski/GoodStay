package org.goodstay.mapper;

import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.model.Room;
import org.goodstay.model.RoomType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RoomMapper {

    public List<RoomListResponseDto> toDto(Map<RoomType, List<Room>> roomsByType) {

        return roomsByType.values()
                .stream()
                .map(roomsOfType -> {
                    Room room = roomsOfType.getFirst();
                    RoomType roomType = room.getRoomType();

                    return new RoomListResponseDto(
                          roomType.getId(),
                          room.getPricePerNight(),
                          roomType.getName(),
                          roomType.getMaxGuests(),
                          roomsOfType.size()
                    );
                })
                .toList();
    }
}
