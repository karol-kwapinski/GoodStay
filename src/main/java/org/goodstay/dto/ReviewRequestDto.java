package org.goodstay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record ReviewRequestDto(

        @NotNull
        @Range(min = 1, max = 10)
        Integer rating,

        @Size(max = 3000)
        String comment,

        @NotNull
        Long hotelId
) {}
