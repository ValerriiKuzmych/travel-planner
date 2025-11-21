package io.github.valeriikuzmych.travelplanner.service;


import io.github.valeriikuzmych.travelplanner.dto.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.util.List;


public interface ITripService {


    List<Trip> getTripsForUser(String emailOrUsername);

    void createTrip(Trip trip);

    Trip getTrip(Long Id);

    List<Trip> getTripsByUserId(Long userId);

    void updateTrip(Long id, Trip updatedTrip);

    void deleteTrip(Long id);

    void createTripForUser(String email, Trip trip);

    TripDetailsDTO getTripDetailsForUser(Long tripId, String email);


}

