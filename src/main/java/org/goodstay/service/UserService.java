package org.goodstay.service;

import org.goodstay.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    void register(RegisterRequestDto request);
    LoginResultDto login(LoginRequestDto request);
    CurrentUserDto getCurrentUser(Authentication authentication);
    List<HotelOwnerResponseDto> getHotelAllOwners();
}
