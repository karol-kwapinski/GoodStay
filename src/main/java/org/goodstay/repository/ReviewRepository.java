package org.goodstay.repository;

import org.goodstay.model.Review;
import org.goodstay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByHotelId(Long hotelId);

    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);
}
