package org.goodstay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 60)
        String password,

        @NotBlank
        String confirmPassword,

        String firstName,

        String lastName,

        String phoneNumber,

        String country
) {}
