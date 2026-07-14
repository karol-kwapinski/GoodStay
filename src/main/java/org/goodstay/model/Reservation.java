package org.goodstay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @NotNull
    @Size(min = 2, max = 60)
    private String guestFirstName;

    @NotNull
    @Size(min = 2, max = 60)
    private String guestLastName;

    @NotNull
    private String guestEmail;

    @NotNull
    private String guestPhoneNumber;

    @NotNull
    private String guestCountry;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reservation_room",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    List<Room> rooms;
}
