package org.goodstay.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int size,
        int page,
        long totalElements,
        int totalPages
) {}
