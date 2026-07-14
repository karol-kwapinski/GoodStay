package org.goodstay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BigDecimal pricePerNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type")
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToMany(mappedBy = "rooms")
    List<Reservation> reservationList;
}
