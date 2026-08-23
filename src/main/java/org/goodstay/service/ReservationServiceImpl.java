package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.*;
import org.goodstay.exception.*;
import org.goodstay.mapper.ReservationMapper;
import org.goodstay.model.Reservation;
import org.goodstay.model.Room;
import org.goodstay.model.RoomType;
import org.goodstay.model.User;
import org.goodstay.repository.ReservationRepository;
import org.goodstay.repository.RoomRepository;
import org.goodstay.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;

    @Transactional
    public void addReservation(
            ReservationRequestDto request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                UserNotFoundException::new
        );

        List<Room> allRooms = getAvailableRooms(
                request.roomTypes(),
                request.hotelId(),
                request.checkInDate(),
                request.checkOutDate()
        );

        BigDecimal totalPrice = calculateTotalPrice(
                request.checkInDate(),
                request.checkOutDate(),
                allRooms
        );

        Reservation reservation = reservationMapper.toEntity(request, allRooms, totalPrice, user);

        reservationRepository.save(reservation);
    }

    public BigDecimal getTotalPrice(Long hotelId, TotalPriceDto dto) {

        List<Room> allRooms = getAvailableRooms(
                dto.roomTypes(),
                hotelId,
                dto.checkInDate(),
                dto.checkOutDate()
        );

        return calculateTotalPrice(
                dto.checkInDate(),
                dto.checkOutDate(),
                allRooms
        );
    }

    public List<ReservationInfoDto> getReservationInfo(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                UserNotFoundException::new
        );

        List<Reservation> reservations = reservationRepository.findByIdWithRoomsAndHotel(
                user.getId());

        return reservationMapper.toReservationInfoDto(reservations);
    }

    public ReservationDetailsDto getReservationDetails(
            Authentication authentication,
            Long reservationId) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                UserNotFoundException::new
        );

        Reservation reservation = reservationRepository
                .findByUserIdAndReservationIdWithRoomsAndHotelAndRoomType(
                        user.getId(),
                        reservationId).orElseThrow();

        Map<RoomType, Long> roomTypeCounts = reservation.getRooms().stream()
                .map(Room::getRoomType)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        List<RoomTypeAndMaxGuestsDto> roomsDetailsList = roomTypeCounts.entrySet().stream()
                .map(roomTypeLongEntry -> new RoomTypeAndMaxGuestsDto(
                            roomTypeLongEntry.getKey().getName(),
                            roomTypeLongEntry.getValue().intValue(),
                            roomTypeLongEntry.getKey().getMaxGuests()
                            ))
                .toList();

        return reservationMapper.toReservationDetailsDto(reservation, roomsDetailsList);

    }

    private List<Room> getAvailableRooms(
            List<RoomTypeSelectionDto> roomTypes,
            Long hotelId,
            LocalDate checkInDate,
            LocalDate checkOutDate) {

        if(!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidDateRangeException();
        }

        Set<Long> roomTypeIds = roomTypes.stream()
                .map(RoomTypeSelectionDto::roomTypeId)
                .collect(Collectors.toSet());

        if (roomTypeIds.size() != roomTypes.size()) {
            throw new InvalidRoomTypeSelectionException();
        }

        List<Room> allRooms = new ArrayList<>();

        for (RoomTypeSelectionDto dto : roomTypes) {

            if (dto.quantity() <= 0) {
                throw new InvalidRoomQuantityException();
            }

            List<Room> availableRooms = roomRepository.getAllRoomsByDatesHotelIdAndRoomTypeId(
                    hotelId,
                    dto.roomTypeId(),
                    checkInDate,
                    checkOutDate
            );

            if (availableRooms.size() < dto.quantity()) {
                throw new RoomNotAvailableException();
            }

            allRooms.addAll(
                    availableRooms.subList(0, dto.quantity())
            );

        }

        return allRooms;
    }

    private BigDecimal calculateTotalPrice(LocalDate checkInDate,
                                           LocalDate checkOutDate,
                                           List<Room> rooms) {

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        return rooms.stream()
                .map(Room::getPricePerNight)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(nights));
    }
}
