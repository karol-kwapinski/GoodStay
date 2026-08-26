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
        ApplicationConfiguration.class
})
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class FacilityIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityRepository facilityRepository;

    private User createUser(String email, String password, UserRole userRole) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(userRole);
        return userRepository.save(user);
    }

    private Facility createFacility(String name) {
        Facility facility = new Facility();
        facility.setName(name);
        return facilityRepository.save(facility);
    }

    private Hotel createHotel(
            String name,
            Integer stars,
            String brand,
            String cityName,
            String street,
            String buildingNumber,
            LocalTime checkInFrom,
            LocalTime checkInUntil,
            LocalTime checkOutUntil,
            User owner,
            List<Facility> facilities
    ) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setStars(stars);
        hotel.setBrand(brand);
        hotel.setCityName(cityName);
        hotel.setStreet(street);
        hotel.setBuildingNumber(buildingNumber);
        hotel.setCheckInFrom(checkInFrom);
        hotel.setCheckInUntil(checkInUntil);
        hotel.setCheckOutUntil(checkOutUntil);
        hotel.setOwner(owner);
        hotel.setFacilities(facilities);
        return hotelRepository.save(hotel);
    }

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

        User owner = createUser(
                "jan.kowalski@gmail.com",
                "password",
                        UserRole.HOTEL_OWNER);

        Facility facility1 = createFacility("Parking");
        Facility facility2 = createFacility("Swimming pool");
        Facility facility3 = createFacility("Hot tub");
        Facility facility4 = createFacility("Free Wifi");
        Facility facility5 = createFacility("Restaurant");
        Facility facility6 = createFacility("Fitness center");
        createFacility("Happy hour");

        Hotel hotel1 = createHotel(
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

        Hotel hotel2 = createHotel(
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

        Hotel hotel3 = createHotel(
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
