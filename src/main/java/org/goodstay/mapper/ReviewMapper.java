package org.goodstay.mapper;

import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.goodstay.model.Hotel;
import org.goodstay.model.Review;
import org.goodstay.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getUser().getEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    public List<ReviewResponseDto> toDto(List<Review> reviews) {
        return reviews.stream()
                .map(this::toDto)
                .toList();
    }

    public Review toEntity(
            ReviewRequestDto request,
            User user,
            Hotel hotel) {
        Review review = new Review();
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setUser(user);
        review.setHotel(hotel);

        return review;
    }
}
