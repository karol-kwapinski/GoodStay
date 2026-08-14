package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelListResponseDto;
import org.goodstay.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping("/getAllHotelsByResAndCity")
    public ResponseEntity<List<HotelListResponseDto>> getAllHotelsByReservationDateAndCityName(
            @Valid @RequestBody HotelListRequestDto request
            ) {

        List<HotelListResponseDto> response = hotelService.getAvailableHotels(request);

        return ResponseEntity.ok(response);
    }
}
