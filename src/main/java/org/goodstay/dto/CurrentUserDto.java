package org.goodstay.dto;

public record CurrentUserDto(
        String email,
        String firstName,
        String role
) {}
