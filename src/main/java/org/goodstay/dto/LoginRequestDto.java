package org.goodstay.dto;

public record LoginRequestDto(
        String email,
        String password
) {}
