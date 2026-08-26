package org.goodstay.repository;

import org.goodstay.model.Facility;
import org.goodstay.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("""
        SELECT DISTINCT h
        FROM Hotel h
        WHERE h.cityName = :cityName
          AND EXISTS (
              SELECT r
              FROM Room r
              WHERE r.hotel = h
                AND r NOT IN (
                    SELECT room
                    FROM Reservation res
                    JOIN res.rooms room
                    WHERE :checkInDate < res.checkOutDate
                      AND :checkOutDate > res.checkInDate
                )
          )
    """)
    List<Hotel> getAvailableHotels(
            @Param("cityName") String cityName,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("""
        SELECT h
        FROM Hotel h
        JOIN h.facilities f
        WHERE f.name IN :facilities
          AND h.cityName = :cityName
          AND EXISTS (
              SELECT r
              FROM Room r
              WHERE r.hotel = h
                AND r NOT IN (
                    SELECT room
                    FROM Reservation res
                    JOIN res.rooms room
                    WHERE :checkInDate < res.checkOutDate
                      AND :checkOutDate > res.checkInDate
                )
          )
          GROUP BY h
          HAVING COUNT(DISTINCT f.name) = :facilityCount
    """)
    List<Hotel> getAvailableHotelsWithFacilities(
            @Param("cityName") String cityName,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("facilities") List<String> facilities,
            @Param("facilityCount") long facilityCount
    );

}























