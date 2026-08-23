package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.ReservationDetailsDto;
import org.goodstay.dto.ReservationInfoDto;
import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.TotalPriceDto;
import org.goodstay.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/addReservation")
    public ResponseEntity<Void> addReservation(
            @Valid @RequestBody ReservationRequestDto request,
            Authentication authentication) {

        reservationService.addReservation(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/getTotalPrice/{hotelId}")
    public ResponseEntity<BigDecimal> getTotalPrice(
            @PathVariable("hotelId") Long hotelId, @Valid @RequestBody TotalPriceDto dto) {
        return ResponseEntity.ok(reservationService.getTotalPrice(hotelId, dto));
    }

    @GetMapping("/getReservations")
    public ResponseEntity<List<ReservationInfoDto>> getReservations(Authentication authentication){
        return ResponseEntity.ok(reservationService.getReservationInfo(authentication));
    }

    @GetMapping("/getReservationDetails/{reservationId}")
    public ResponseEntity<ReservationDetailsDto> getReservationDetails(
            Authentication authentication,
            @PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.ok(reservationService.getReservationDetails(authentication, reservationId));
    }
}
