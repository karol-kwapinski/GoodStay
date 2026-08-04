package org.goodstay.dto;

public record LoginResultDto(
        String token,
        CurrentUserDto user,
        long expirationTime
) {}
