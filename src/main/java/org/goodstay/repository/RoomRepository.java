package org.goodstay.repository;

import org.goodstay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        SELECT r
        FROM Room r
        JOIN FETCH r.roomType
        WHERE r.hotel.id = :hotelId
        AND r NOT IN(
            SELECT room
            FROM Reservation res
            JOIN res.rooms room
            WHERE :checkInDate < res.checkOutDate
            AND :checkOutDate > res.checkInDate    
        )
    """)
    public List<Room> getAllRoomsByDatesAndHotelId(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);
}
