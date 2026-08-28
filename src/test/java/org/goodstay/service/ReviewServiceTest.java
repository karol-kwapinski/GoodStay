package org.goodstay.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    static Stream<Arguments> getValidRequests() {

        ReviewRequestDto request1 = new ReviewRequestDto(
                7,
                "Nice hotel",
                1L
        );

        ReviewRequestDto request2 = new ReviewRequestDto(
                3,
                "",
                1L
        );

        ReviewRequestDto request3 = new ReviewRequestDto(
                3,
                null,
                1L
        );

        return Stream.of(
                Arguments.of(
                        "Comment is not empty",
                        request1
                ),
                Arguments.of(
                        "Comment is empty",
                        request2
                ),
                Arguments.of(
                        "Comment is null",
                        request3
                )
        );
    }

    private ReviewRequestDto createValidReviewRequest() {
        return new ReviewRequestDto(
                6,
                "Good.",
                1L
        );
    }

    @Test
    void shouldReturnReviews() {

        User user1 = new User();
        user1.setEmail("jan.kowalski@gmail.com");

        User user2 = new User();
        user2.setEmail("jan.nowak@gmail.com");

        User user3 = new User();
        user3.setEmail("agata.nowakowska@gmail.com");

        Review review1 = new Review();
        review1.setComment("Pretty good hotel");
        review1.setCreatedAt(LocalDateTime.of(2026, 8, 9, 15, 30, 22));
        review1.setRating(7);
        review1.setUser(user1);

        Review review2 = new Review();
        review2.setComment("Very bad hotel");
        review2.setCreatedAt(LocalDateTime.of(2026, 8, 10, 17, 30, 22));
        review2.setRating(2);
        review2.setUser(user2);

        Review review3 = new Review();
        review3.setCreatedAt(LocalDateTime.of(2026, 11, 6, 20, 40, 0));
        review3.setRating(8);
        review3.setUser(user3);

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

        when(reviewRepository.findReviewsByHotelId(1L))
                .thenReturn(List.of(review1, review2, review3));

        when(reviewMapper.toDto(List.of(review1, review2, review3)))
                .thenReturn(expected);

        List<ReviewResponseDto> response = reviewService.getAllReviews(1L);

        assertEquals(expected, response);

        verify(reviewRepository).findReviewsByHotelId(1L);
        verify(reviewMapper).toDto(List.of(review1, review2, review3));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getValidRequests")
    void shouldCreateReview(
            String description,
            ReviewRequestDto request) {

        User user = new User();
        user.setId(1L);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        Review review = new Review();
        review.setUser(user);
        review.setHotel(hotel);
        review.setComment(request.comment());
        review.setRating(request.rating());

        ReviewResponseDto expected = new ReviewResponseDto(
                "jan.kowalski@gmail.com",
                request.rating(),
                request.comment(),
                LocalDateTime.of(2026, 8, 10, 22, 33, 12)
        );

        when(authentication.getName())
                .thenReturn("jan.kowalski@gmail.com");

        when(userRepository.findByEmail("jan.kowalski@gmail.com"))
                .thenReturn(Optional.of(user));

        when(reviewRepository.existsByUserIdAndHotelId(user.getId(), request.hotelId()))
                .thenReturn(false);

        when(hotelRepository.findById(request.hotelId()))
                .thenReturn(Optional.of(hotel));

        when(reviewMapper.toEntity(request, user, hotel))
                .thenReturn(review);

        when(reviewRepository.save(review))
                .thenReturn(review);

        when(reviewMapper.toDto(review))
                .thenReturn(expected);

        ReviewResponseDto response = reviewService.addReview(authentication, request);

        assertEquals(expected, response);

        verify(authentication).getName();
        verify(userRepository).findByEmail("jan.kowalski@gmail.com");
        verify(hotelRepository).findById(request.hotelId());
        verify(reviewRepository).existsByUserIdAndHotelId(user.getId(), request.hotelId());
        verify(reviewMapper).toEntity(request, user, hotel);
        verify(reviewRepository).save(review);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        ReviewRequestDto request = createValidReviewRequest();

        assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(authentication, request));

        verify(authentication).getName();
        verify(userRepository).findByEmail(null);
        verifyNoInteractions(hotelRepository);
        verifyNoInteractions(reviewRepository);
        verifyNoInteractions(reviewMapper);
    }

    @Test
    void shouldThrowHotelDoesNotExistException() {
        ReviewRequestDto request = createValidReviewRequest();

        User user = new User();
        user.setId(1L);

        when(authentication.getName())
                .thenReturn("jan.kowalski@gmail.com");

        when(userRepository.findByEmail("jan.kowalski@gmail.com"))
                .thenReturn(Optional.of(user));

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(HotelDoesNotExistException.class,
                () -> reviewService.addReview(authentication, request));

        verify(authentication).getName();
        verify(userRepository).findByEmail("jan.kowalski@gmail.com");
        verify(hotelRepository).findById(1L);
        verifyNoInteractions(reviewMapper);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void shouldThrowReviewAlreadyExistsException() {

        ReviewRequestDto request = createValidReviewRequest();

        User user = new User();
        user.setId(1L);

        Hotel hotel = new Hotel();
        hotel.setId(request.hotelId());

        when(authentication.getName())
                .thenReturn("jan.kowalski@gmail.com");

        when(userRepository.findByEmail("jan.kowalski@gmail.com"))
                .thenReturn(Optional.of(user));

        when(hotelRepository.findById(hotel.getId()))
                .thenReturn(Optional.of(hotel));

        when(reviewRepository.existsByUserIdAndHotelId(user.getId(), hotel.getId()))
                .thenReturn(true);

        assertThrows(ReviewAlreadyExistsException.class,
                () -> reviewService.addReview(authentication, request));

        verify(authentication).getName();
        verify(userRepository).findByEmail("jan.kowalski@gmail.com");
        verify(hotelRepository).findById(hotel.getId());
        verify(reviewRepository).existsByUserIdAndHotelId(user.getId(), hotel.getId());
        verifyNoInteractions(reviewMapper);
        verify(reviewRepository, never()).save(any(Review.class));
    }

}
