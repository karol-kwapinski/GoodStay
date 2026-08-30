package org.goodstay.util;

import lombok.RequiredArgsConstructor;
import org.goodstay.model.*;
import org.goodstay.repository.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final FacilityRepository facilityRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public User createUser(String email, UserRole role) {
        User user = new User();
        user.setRole(role);
        user.setPassword("password");
        user.setEmail(email);

        return userRepository.save(user);
    }

    public Hotel createHotelWithData(User owner) {

        return createHotel(
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
    }

    public Hotel createHotel(
            String name,
            Integer stars,
            String brand,
            String cityName,
            String street,
            String buildingNumber,
            LocalTime checkInFrom,
            LocalTime checkInUntil,
            LocalTime checkOutUntil,
            User owner
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

        return hotelRepository.save(hotel);
    }

    public Hotel createHotelWithFacilities(
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

    public Facility createFacility(String name) {
        Facility facility = new Facility();
        facility.setName(name);
        return facilityRepository.save(facility);
    }

    public Facility createFacilityWithHotels(String name, List<Hotel> hotels) {
        Facility facility = new Facility();
        facility.setName(name);
        facility.setHotels(hotels);
        return facilityRepository.save(facility);
    }

    public Review createReview(Integer rating, String comment, Hotel hotel, User user) {
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setHotel(hotel);
        review.setUser(user);

        return reviewRepository.save(review);
    }

    public RoomType createRoomType(String name, Integer maxGuests) {
        RoomType roomType = new RoomType();
        roomType.setName(name);
        roomType.setMaxGuests(maxGuests);

        return roomTypeRepository.save(roomType);
    }

    public Room createRoom(RoomType roomType, Hotel hotel, BigDecimal totalPrice) {
        Room room = new Room();
        room.setPricePerNight(totalPrice);
        room.setRoomType(roomType);
        room.setHotel(hotel);

        return roomRepository.save(room);
    }

    public Reservation createReservation(
            List<Room> rooms,
            User user,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            String guestEmail,
            String guestFirstName,
            String guestLastName,
            String guestCountry,
            String guestPhoneNumber,
            ReservationStatus reservationStatus) {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setGuestEmail(guestEmail);
        reservation.setGuestFirstName(guestFirstName);
        reservation.setGuestLastName(guestLastName);
        reservation.setGuestCountry(guestCountry);
        reservation.setGuestPhoneNumber(guestPhoneNumber);
        reservation.setStatus(reservationStatus);

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        BigDecimal totalPrice = rooms.stream()
                .map(Room::getPricePerNight)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(nights));

        reservation.setTotalPrice(totalPrice);
        reservation.setRooms(rooms);
        reservation.setUser(user);

        return reservationRepository.save(reservation);
    }
}
