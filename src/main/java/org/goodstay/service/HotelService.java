package org.goodstay.service;

import org.goodstay.dto.*;

import java.util.List;

public interface HotelService {

    List<HotelResponseDto> getAvailableHotels(HotelListRequestDto request);
    HotelResponseDto getHotel(Long hotelId);
    HotelBasicInfoDto addHotel(AddHotelRequestDto request);
    PageResponse<HotelBasicInfoDto> getAllHotels(int page, int size);
}
