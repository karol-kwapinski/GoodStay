package org.goodstay.service;

import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDto> getAllReviews(Long hotelId);
    ReviewResponseDto addReview(Authentication authentication, ReviewRequestDto request);
}
