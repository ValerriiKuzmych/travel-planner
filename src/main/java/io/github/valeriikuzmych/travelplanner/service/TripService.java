package io.github.valeriikuzmych.travelplanner.service;


import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.util.List;


public interface TripService {


    Trip createTrip(TripForm form, String userEmail);

    Trip updateTrip(Long id, TripForm form, String userEmail);

    Trip getTrip(Long id, String userEmail);

    List<TripBasicDTO> getTripsForUser(String email);

    TripDetailsDTO getTripDetails(Long id, String email);

    TripPlanDTO getTripPlan(Long id, String email);

    void deleteTrip(Long id, String email);

}

