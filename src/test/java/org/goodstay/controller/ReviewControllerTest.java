package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goodstay.dto.ReviewRequestDto;
import org.goodstay.dto.ReviewResponseDto;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.exception.HotelDoesNotExistException;
import org.goodstay.exception.ReviewAlreadyExistsException;
import org.goodstay.exception.UserNotFoundException;
import org.goodstay.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ReviewRequestDto createValidRequest() {
        return new ReviewRequestDto(
                8,
                "Nice hotel",
                1L
        );
    }

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(reviewController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnReviews() throws Exception {

        ReviewResponseDto result1 = new ReviewResponseDto(
                "jan.kowalski@gmail.com",
                6,
                "Pretty good",
                LocalDateTime.of(2026, 8, 10, 15, 30, 22)
        );

        ReviewResponseDto result2 = new ReviewResponseDto(
                "agata.nowak@interia.pl",
                2,
                "Terrible",
                LocalDateTime.of(2026, 9, 10, 12, 15, 10)
        );

        when(reviewService.getAllReviews(1L))
                .thenReturn(List.of(result1, result2));

        mockMvc.perform(get("/api/reviews/getReviews/{hotelId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value(result1.userName()))
                .andExpect(jsonPath("$[0].rating").value(result1.rating()))
                .andExpect(jsonPath("$[0].comment").value(result1.comment()))
                .andExpect(jsonPath("$[0].createdAt").value(
                        result1.createdAt().toLocalDate().toString() + " "
                        + result1.createdAt().toLocalTime().toString()
                ))
                .andExpect(jsonPath("$[1].userName").value(result2.userName()))
                .andExpect(jsonPath("$[1].rating").value(result2.rating()))
                .andExpect(jsonPath("$[1].comment").value(result2.comment()))
                .andExpect(jsonPath("$[1].createdAt").value(
                        result2.createdAt().toLocalDate().toString() + " "
                                + result2.createdAt().toLocalTime().toString()
                ));

        verify(reviewService).getAllReviews(1L);
    }

    @Test
    void shouldCreateReview() throws Exception {

        ReviewRequestDto request = createValidRequest();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.kowalski@gmail.com",
                null
        );

        mockMvc.perform(post("/api/reviews/addReview")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());

        verify(reviewService).addReview(authentication, request);
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFoundExceptionIsThrown() throws Exception{

        ReviewRequestDto request = createValidRequest();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.kowalski@gmail.com",
                null
        );

        doThrow(new UserNotFoundException())
                .when(reviewService)
                        .addReview(authentication, request);

        mockMvc.perform(post("/api/reviews/addReview")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(reviewService).addReview(authentication, request);
    }

    @Test
    void shouldReturnNotFoundWhenHotelDoesNotExistExceptionIsThrown() throws Exception{

        ReviewRequestDto request = createValidRequest();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.kowalski@gmail.com",
                null
        );

        doThrow(new HotelDoesNotExistException())
                .when(reviewService)
                .addReview(authentication, request);

        mockMvc.perform(post("/api/reviews/addReview")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(reviewService).addReview(authentication, request);
    }

    @Test
    void shouldReturnConflictWhenReviewAlreadyExistExceptionIsThrown() throws Exception{

        ReviewRequestDto request = createValidRequest();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "jan.kowalski@gmail.com",
                null
        );

        doThrow(new ReviewAlreadyExistsException())
                .when(reviewService)
                .addReview(authentication, request);

        mockMvc.perform(post("/api/reviews/addReview")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(reviewService).addReview(authentication, request);
    }

}
