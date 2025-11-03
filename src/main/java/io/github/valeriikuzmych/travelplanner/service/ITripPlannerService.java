package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;

public interface ITripPlannerService {

    TripPlanDTO getPlanForTrip(Long tripId);
    
}
