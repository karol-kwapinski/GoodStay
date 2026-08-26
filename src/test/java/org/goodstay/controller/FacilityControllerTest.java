package org.goodstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goodstay.exception.GlobalExceptionHandler;
import org.goodstay.service.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FacilityControllerTest {

    @Mock
    private FacilityService facilityService;

    @InjectMocks
    private FacilityController facilityController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(facilityController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnFacilities() throws Exception {

        when(facilityService.getFacilities(List.of(1L, 2L, 3L)))
                .thenReturn(List.of("Parking", "Swimming pool", "Hot tub"));

        mockMvc.perform(get("/api/facilities/getFacilities")
                .param("hotelIds", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Parking"))
                .andExpect(jsonPath("$[1]").value("Swimming pool"))
                .andExpect(jsonPath("$[2]").value("Hot tub"));

        verify(facilityService).getFacilities(List.of(1L, 2L, 3L));
    }
}
