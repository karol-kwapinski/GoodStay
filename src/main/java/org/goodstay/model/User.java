package org.goodstay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 30)
    private String email;

    @NotNull
    private String password;

    @Size(min = 2, max = 60)
    private String firstName;

    @Size(min = 2, max = 60)
    private String lastName;

    @Size(min = 2, max = 60)
    private String phoneNumber;

    @Size(min = 2, max = 60)
    private String country;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "owner")
    private List<Hotel> hotels;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;
}
