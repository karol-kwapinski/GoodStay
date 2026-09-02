package org.goodstay.service;

import org.goodstay.dto.*;

import java.util.List;

public interface HotelService {

    List<HotelResponseDto> getAvailableHotels(HotelListRequestDto request);
    HotelResponseDto getHotel(Long hotelId);
    HotelResponseFullDataDto getHotelWithFullData(Long hotelId);
    HotelBasicInfoDto addHotel(HotelRequestDto request);
    HotelBasicInfoDto editHotel(Long hotelId, HotelRequestDto request);
    PageResponse<HotelBasicInfoDto> getAllHotels(int page, int size);
}
