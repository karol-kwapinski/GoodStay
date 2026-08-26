package org.goodstay.repository;

import org.goodstay.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    @Query(
     """
        SELECT DISTINCT f
        FROM Facility f
        JOIN f.hotels hotel
        WHERE hotel.id IN :hotelIds
        ORDER BY f.name
     """
    )
    List<Facility> findFacilitiesByHotelIds(@Param("hotelIds") List<Long> hotelIds);
}
