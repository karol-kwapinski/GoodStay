package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.RoomListRequestDto;
import org.goodstay.dto.RoomListResponseDto;
import org.goodstay.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/getAllRoomsByDatesAndHotelId/{hotelId}")
    public ResponseEntity<List<RoomListResponseDto>> getAllRoomsByDatesAndHotelId(
            @PathVariable("hotelId") Long hotelId,
            @Valid @ModelAttribute RoomListRequestDto request
            ) {

        List<RoomListResponseDto> response = roomService.getAllRoomsByDatesAndHotelId(
                hotelId,
                request
        );

        return ResponseEntity.ok(response);
    }
}
