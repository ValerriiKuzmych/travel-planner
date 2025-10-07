package io.github.valeriikuzmych.travelplanner.entity;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name ="user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip trip)) return false;
        return Objects.equals(id, trip.id) && Objects.equals(user, trip.user) && Objects.equals(city, trip.city) && Objects.equals(startDate, trip.startDate) && Objects.equals(endDate, trip.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, city, startDate, endDate);
    }
}
