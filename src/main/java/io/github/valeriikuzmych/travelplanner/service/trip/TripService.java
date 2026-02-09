package io.github.valeriikuzmych.travelplanner.service.trip;


import io.github.valeriikuzmych.travelplanner.dto.trip.TripResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.util.List;


public interface TripService {


    Trip createTrip(TripForm form, String userEmail);

    Trip updateTrip(Long id, TripForm form, String userEmail);

    Trip getTrip(Long id, String userEmail);

    List<TripResponse> getTripsForUser(String email);

    TripDetailsResponse getTripDetails(Long id, String email);

    TripPlanDTO getTripPlan(Long id, String email);

    void deleteTrip(Long id, String email);

}

