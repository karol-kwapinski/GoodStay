package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.LoginRequestDto;
import org.goodstay.dto.CurrentUserDto;
import org.goodstay.dto.LoginResultDto;
import org.goodstay.dto.RegisterRequestDto;
import org.goodstay.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {

        userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<CurrentUserDto> login(@RequestBody LoginRequestDto request) {

        LoginResultDto loginResultDto = userService.login(request);

        ResponseCookie cookie = ResponseCookie.from("accessToken", loginResultDto.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(loginResultDto.expirationTime()))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResultDto.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }
}