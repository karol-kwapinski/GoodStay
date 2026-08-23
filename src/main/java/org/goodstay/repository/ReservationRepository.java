package org.goodstay.repository;

import org.goodstay.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> getReservationById(Long id);

    @Query("""
        SELECT r
        FROM Reservation r
        JOIN FETCH r.rooms room
        JOIN FETCH room.hotel
        WHERE r.user.id = :userId
    """)
    List<Reservation> findByIdWithRoomsAndHotel(@Param("userId") Long userId);

    @Query("""
        SELECT r
        FROM Reservation r
        JOIN FETCH r.rooms room
        JOIN FETCH room.hotel
        JOIN FETCH room.roomType
        WHERE r.user.id = :userId
        AND r.id = :reservationId
    """)
    Optional<Reservation> findByUserIdAndReservationIdWithRoomsAndHotelAndRoomType(
            @Param("userId") Long userId,
            @Param("reservationId") Long reservationId
    );
}
