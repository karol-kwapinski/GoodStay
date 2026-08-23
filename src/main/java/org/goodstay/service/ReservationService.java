package org.goodstay.service;

import org.goodstay.dto.ReservationDetailsDto;
import org.goodstay.dto.ReservationInfoDto;
import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.TotalPriceDto;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface ReservationService {
    void addReservation(ReservationRequestDto request, Authentication authentication);
    BigDecimal getTotalPrice(Long hotelId, TotalPriceDto dto);
    List<ReservationInfoDto> getReservationInfo(Authentication authentication);
    ReservationDetailsDto getReservationDetails(Authentication authentication, Long reservationId);
}
