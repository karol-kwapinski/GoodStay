package org.goodstay.integrationTests;

import org.goodstay.configuration.ApplicationConfiguration;
import org.goodstay.model.Facility;
import org.goodstay.model.Hotel;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.repository.FacilityRepository;
import org.goodstay.repository.HotelRepository;
import org.goodstay.repository.UserRepository;
import org.goodstay.service.FacilityService;
import org.goodstay.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ApplicationConfiguration.class,
        TestDataFactory.class
})
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class FacilityIntegrationTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private FacilityService facilityService;

    @Test
    void shouldFetchFacilitiesBasedOnHotelIds() {

        List<String> expected = List.of(
                "Fitness center",
                "Free Wifi",
                "Hot tub",
                "Parking",
                "Restaurant",
                "Swimming pool"
        );

        User owner = testDataFactory.createUser(
                "jan.kowalski@gmail.com",
                        UserRole.HOTEL_OWNER);

        Facility facility1 = testDataFactory.createFacility("Parking");
        Facility facility2 = testDataFactory.createFacility("Swimming pool");
        Facility facility3 = testDataFactory.createFacility("Hot tub");
        Facility facility4 = testDataFactory.createFacility("Free Wifi");
        Facility facility5 = testDataFactory.createFacility("Restaurant");
        Facility facility6 = testDataFactory.createFacility("Fitness center");
        testDataFactory.createFacility("Happy hour");

        Hotel hotel1 = testDataFactory.createHotelWithFacilities(
                "Warsaw hotel",
                3,
                "Good hotels",
                "Warsaw",
                "Mickiewicza",
                "15C",
                LocalTime.of(14, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(11, 0, 0),
                owner,
                List.of(facility1, facility2, facility3)
        );

        Hotel hotel2 = testDataFactory.createHotelWithFacilities(
                "Cracow hotel",
                4,
                "Good hotels",
                "Cracow",
                "Nowaka",
                "6C",
                LocalTime.of(14, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(10, 0, 0),
                owner,
                List.of(facility1, facility5)
        );

        Hotel hotel3 = testDataFactory.createHotelWithFacilities(
                "Poznan hotel",
                3,
                "Good hotels",
                "Poznan",
                "Kowalskiego",
                "83K",
                LocalTime.of(14, 0, 0),
                LocalTime.of(21, 0, 0),
                LocalTime.of(12, 0, 0),
                owner,
                List.of(facility4, facility5, facility6)
        );

        List<String> facilities = facilityService.getFacilities(
                List.of(
                        hotel1.getId(),
                        hotel2.getId(),
                        hotel3.getId()
                )
        );

        assertEquals(expected, facilities);
    }
}
