package org.goodstay.service;

import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.TotalPriceDto;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface ReservationService {
    void addReservation(ReservationRequestDto request, Authentication authentication);
    BigDecimal getTotalPrice(Long hotelId, TotalPriceDto dto);
}
