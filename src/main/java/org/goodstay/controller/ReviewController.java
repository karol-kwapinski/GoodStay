package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.goodstay.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/getReviews/{hotelId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable("hotelId") Long hotelId) {
        return ResponseEntity.ok(reviewService.getAllReviews(hotelId));
    }

    @PostMapping("/addReview")
    public ResponseEntity<ReviewResponseDto> addReview(Authentication authentication,
                                          @Valid @RequestBody ReviewRequestDto request) {

        return ResponseEntity.ok(reviewService.addReview(authentication, request));
    }
}
