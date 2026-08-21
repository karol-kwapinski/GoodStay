package org.goodstay.dto;

public record CurrentUserDto(
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String country,
        String role
) {}
