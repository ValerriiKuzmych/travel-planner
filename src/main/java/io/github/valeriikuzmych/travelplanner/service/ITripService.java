package io.github.valeriikuzmych.travelplanner.service;


import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.util.List;


public interface ITripService {

    void createTrip(Trip trip);

    Trip getTrip(Long Id);

    List<Trip> getTripsByUserId(Long userId);

    void updateTrip(Long id, Trip updatedTrip);

    void deleteTrip(Long id);


}

