package org.goodstay.service;

import org.goodstay.model.Facility;
import org.goodstay.repository.FacilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacilityServiceTest {

    @Mock
    FacilityRepository facilityRepository;

    @InjectMocks
    FacilityServiceImpl facilityService;

    @Test
    void shouldReturnFacilities() {

        Facility facility1 = new Facility();
        Facility facility2 = new Facility();
        Facility facility3 = new Facility();
        Facility facility4 = new Facility();
        Facility facility5 = new Facility();
        Facility facility6 = new Facility();

        facility1.setName("Parking");
        facility2.setName("Swimming pool");
        facility3.setName("Hot tub");
        facility4.setName("Fitness center");
        facility5.setName("Restaurant");
        facility6.setName("Free Wifi");

        List<String> expected = List.of(
                "Parking",
                "Swimming pool",
                "Hot tub",
                "Fitness center",
                "Restaurant",
                "Free Wifi"
        );

        when(facilityRepository.findFacilitiesByHotelIds(List.of(1L, 2L, 3L, 4L, 5L)))
                .thenReturn(List.of(
                        facility1,
                        facility2,
                        facility3,
                        facility4,
                        facility5,
                        facility6
                ));

        List<String> facilities = facilityService.getFacilities(List.of(1L, 2L, 3L, 4L, 5L));

        assertEquals(expected, facilities);

        verify(facilityRepository).findFacilitiesByHotelIds(List.of(1L, 2L, 3L, 4L, 5L));
    }
}
