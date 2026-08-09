package org.goodstay.service;

import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelListResponseDto;

import java.util.List;

public interface HotelService {

    List<HotelListResponseDto> getAvailableHotels(HotelListRequestDto request);
}
