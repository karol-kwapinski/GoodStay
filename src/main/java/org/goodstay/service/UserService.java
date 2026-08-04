package org.goodstay.service;

import org.goodstay.dto.LoginRequestDto;
import org.goodstay.dto.CurrentUserDto;
import org.goodstay.dto.LoginResultDto;
import org.goodstay.dto.RegisterRequestDto;
import org.springframework.security.core.Authentication;

public interface UserService {

    void register(RegisterRequestDto request);
    LoginResultDto login(LoginRequestDto request);
    CurrentUserDto getCurrentUser(Authentication authentication);
}
