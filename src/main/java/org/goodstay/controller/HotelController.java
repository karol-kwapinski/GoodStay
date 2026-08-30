package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.AddHotelRequestDto;
import org.goodstay.dto.HotelBasicInfoDto;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelResponseDto;
import org.goodstay.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/getAllHotelsByResAndCity")
    public ResponseEntity<List<HotelResponseDto>> getAllHotelsByReservationDateAndCityName(
            @Valid @ModelAttribute HotelListRequestDto request
            ) {

        List<HotelResponseDto> response = hotelService.getAvailableHotels(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getHotel/{hotelId}")
    public ResponseEntity<HotelResponseDto> getHotel(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotel(hotelId));
    }

    @PostMapping("/addHotel")
    public ResponseEntity<HotelBasicInfoDto> addHotel(@Valid @RequestBody AddHotelRequestDto request) {
        return ResponseEntity.ok(hotelService.addHotel(request));
    }
}
