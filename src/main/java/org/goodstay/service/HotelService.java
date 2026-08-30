package org.goodstay.service;

import org.goodstay.dto.AddHotelRequestDto;
import org.goodstay.dto.HotelBasicInfoDto;
import org.goodstay.dto.HotelListRequestDto;
import org.goodstay.dto.HotelResponseDto;

import java.util.List;

public interface HotelService {

    List<HotelResponseDto> getAvailableHotels(HotelListRequestDto request);
    HotelResponseDto getHotel(Long hotelId);
    HotelBasicInfoDto addHotel(AddHotelRequestDto request);
}
