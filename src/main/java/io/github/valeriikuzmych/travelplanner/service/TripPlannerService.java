package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;

public interface TripPlannerService {

    TripPlanDTO getPlanForTrip(Long tripId);

}
