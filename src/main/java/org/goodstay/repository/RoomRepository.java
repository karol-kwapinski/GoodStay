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
    List<Room> getAllRoomsByDatesAndHotelId(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    @Query("""
        SELECT r
        FROM Room r
        WHERE r.id IN :roomIds
        AND r NOT IN (
            SELECT room
            FROM Reservation res
            JOIN res.rooms room
            WHERE :checkInDate < res.checkOutDate
            AND :checkOutDate > res.checkInDate
        )
    """)
    List<Room> findAvailableRooms(
            @Param("roomIds") List<Long> roomIds,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("""
        SELECT r
        FROM Room r
        WHERE r.roomType.id = :roomTypeId
        AND r.hotel.id = :hotelId 
        AND r NOT IN(
           SELECT room
           FROM Reservation res
           JOIN res.rooms room
           WHERE :checkInDate < res.checkOutDate
           AND :checkOutDate > res.checkInDate   
        )
    """)
    List<Room> getAllRoomsByDatesHotelIdAndRoomTypeId(
            @Param("hotelId") Long hotelId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
