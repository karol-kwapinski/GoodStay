package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.goodstay.exception.HotelDoesNotExistException;
import org.goodstay.exception.ReviewAlreadyExistsException;
import org.goodstay.exception.UserNotFoundException;
import org.goodstay.mapper.ReviewMapper;
import org.goodstay.model.Hotel;
import org.goodstay.model.Review;
import org.goodstay.model.User;
import org.goodstay.repository.HotelRepository;
import org.goodstay.repository.ReviewRepository;
import org.goodstay.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews(Long hotelId) {

        List<Review> reviews = reviewRepository.findReviewsByHotelId(hotelId);

        return reviewMapper.toDto(reviews);
    }

    @Transactional
    public ReviewResponseDto addReview(Authentication authentication, ReviewRequestDto request) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                UserNotFoundException::new
        );

        Hotel hotel = hotelRepository.findById(request.hotelId()).orElseThrow(
                HotelDoesNotExistException::new
        );

        if (reviewRepository.existsByUserIdAndHotelId(user.getId(), request.hotelId())) {
            throw new ReviewAlreadyExistsException();
        }

        Review review = reviewRepository.save(reviewMapper.toEntity(request, user, hotel));

        hotel.setNumberOfRatings(hotel.getNumberOfRatings() + 1);

        return reviewMapper.toDto(review);
    }
}
