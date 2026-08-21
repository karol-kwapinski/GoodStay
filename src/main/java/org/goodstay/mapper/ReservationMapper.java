package org.goodstay.mapper;

import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.model.Reservation;
import org.goodstay.model.ReservationStatus;
import org.goodstay.model.Room;
import org.goodstay.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ReservationMapper {

    public Reservation toEntity(
            ReservationRequestDto request,
            List<Room> rooms,
            BigDecimal totalPrice,
            User user) {

        Reservation reservation = new Reservation();

        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setGuestEmail(request.email());
        reservation.setGuestFirstName(request.firstName());
        reservation.setGuestLastName(request.lastName());
        reservation.setGuestCountry(request.country());
        reservation.setGuestPhoneNumber(request.phoneNumber());
        reservation.setStatus(ReservationStatus.PAID);
        reservation.setTotalPrice(totalPrice);
        reservation.setRooms(rooms);
        reservation.setUser(user);

        return reservation;
    }
}
