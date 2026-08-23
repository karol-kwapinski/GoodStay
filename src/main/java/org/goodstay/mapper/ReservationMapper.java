package org.goodstay.mapper;

import org.goodstay.dto.ReservationDetailsDto;
import org.goodstay.dto.ReservationInfoDto;
import org.goodstay.dto.ReservationRequestDto;
import org.goodstay.dto.RoomTypeAndMaxGuestsDto;
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

    public ReservationInfoDto toReservationInfoDto(Reservation reservation) {
        return new ReservationInfoDto(
                reservation.getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getRooms().getFirst().getHotel().getCityName(),
                reservation.getRooms().getFirst().getHotel().getName()
        );
    }

    public List<ReservationInfoDto> toReservationInfoDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::toReservationInfoDto)
                .toList();
    }

    public ReservationDetailsDto toReservationDetailsDto(
            Reservation reservation,
            List<RoomTypeAndMaxGuestsDto> roomDetailsList) {
        return new ReservationDetailsDto(
                reservation.getRooms().getFirst().getHotel().getCheckInFrom(),
                reservation.getRooms().getFirst().getHotel().getCheckInUntil(),
                reservation.getRooms().getFirst().getHotel().getCheckOutUntil(),
                reservation.getGuestFirstName(),
                reservation.getGuestLastName(),
                reservation.getGuestEmail(),
                reservation.getGuestPhoneNumber(),
                reservation.getGuestCountry(),
                reservation.getTotalPrice(),
                reservation.getCreatedAt(),
                reservation.getStatus().name(),
                roomDetailsList
        );
    }
}
