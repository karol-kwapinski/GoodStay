package org.goodstay.integrationTests;

import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.goodstay.exception.HotelDoesNotExistException;
import org.goodstay.exception.ReviewAlreadyExistsException;
import org.goodstay.exception.UserNotFoundException;
import org.goodstay.model.Hotel;
import org.goodstay.model.Review;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.repository.ReviewRepository;
import org.goodstay.service.ReviewService;
import org.goodstay.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ApplicationConfiguration.class,
        TestDataFactory.class
})
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class ReviewIntegrationTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    private ReviewRequestDto createValidReviewRequest() {
        return new ReviewRequestDto(
                6,
                "Good.",
                1L
        );
    }

    @Test
    void shouldReturnReviews() {

        User owner = testDataFactory.createUser("adam.gonkowski@gmail.com", UserRole.HOTEL_OWNER);
        User user1 = testDataFactory.createUser("jan.kowalski@gmail.com", UserRole.USER);
        User user2 = testDataFactory.createUser("jan.nowak@gmail.com", UserRole.USER);
        User user3 = testDataFactory.createUser("agata.nowakowska@gmail.com", UserRole.USER);

        Hotel hotel = testDataFactory.createHotel(
                "Good hotel",
                5,
                "Great hotels",
                "Warsaw",
                "Mickiewicza",
                "14B",
                LocalTime.of(12, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(15, 0, 0),
                owner
        );

        Review review1 = testDataFactory.createReview(
                7,
                "Pretty Good hotel",
                hotel,
                user1
        );

        Review review2 = testDataFactory.createReview(
                2,
                "Really bad hotel",
                hotel,
                user2
        );

        Review review3 = testDataFactory.createReview(
                8,
                "",
                hotel,
                user3
        );

        List<ReviewResponseDto> expected = List.of(
                new ReviewResponseDto(
                        review1.getUser().getEmail(),
                        review1.getRating(),
                        review1.getComment(),
                        review1.getCreatedAt()
                ),
                new ReviewResponseDto(
                        review2.getUser().getEmail(),
                        review2.getRating(),
                        review2.getComment(),
                        review2.getCreatedAt()
                ),
                new ReviewResponseDto(
                        review3.getUser().getEmail(),
                        review3.getRating(),
                        review3.getComment(),
                        review3.getCreatedAt()
                )
        );


        List<ReviewResponseDto> response = reviewService.getAllReviews(hotel.getId());

        assertEquals(expected, response);
    }

    @Test
    void shouldCreateReview() {

        User owner = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.HOTEL_OWNER
        );

        Hotel hotel = testDataFactory.createHotelWithData(owner);

        User user = testDataFactory.createUser(
                "adam.bonkowski@gmail.com",
                UserRole.USER
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        ReviewRequestDto request = new ReviewRequestDto(
                9,
                "Great hotel, had really good time.",
                hotel.getId()
        );

        ReviewResponseDto response = reviewService.addReview(authentication, request);
        List<Review> reviews = reviewRepository.findReviewsByHotelId(hotel.getId());
        Review saved = reviews.getFirst();

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals(request.rating(), saved.getRating());
        assertEquals(request.comment(), saved.getComment());
        assertEquals(request.hotelId(), saved.getHotel().getId());
        assertEquals(user, saved.getUser());

        ReviewResponseDto expected = new ReviewResponseDto(
                user.getEmail(),
                request.rating(),
                request.comment(),
                saved.getCreatedAt());

        assertEquals(expected, response);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        ReviewRequestDto request = createValidReviewRequest();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                null
        );

        assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(authentication, request));
    }

    @Test
    void shouldThrowHotelDoesNotExistException() {
        ReviewRequestDto request = createValidReviewRequest();

        User user = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.USER
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        assertThrows(HotelDoesNotExistException.class,
                () -> reviewService.addReview(authentication, request));
    }

    @Test
    void shouldThrowReviewAlreadyExistsException() {

        User owner = testDataFactory.createUser(
                "adam.nowak@interia.pl",
                UserRole.HOTEL_OWNER
        );

        User user = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                UserRole.USER
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null
        );

        Hotel hotel = testDataFactory.createHotelWithData(owner);

        ReviewRequestDto request = new ReviewRequestDto(
                8,
                "Amazing.",
                hotel.getId()
        );

        testDataFactory.createReview(
                5,
                "Not good, not bad, mediocre",
                hotel,
                user
        );

        assertThrows(ReviewAlreadyExistsException.class,
                () -> reviewService.addReview(authentication, request));
    }
}
