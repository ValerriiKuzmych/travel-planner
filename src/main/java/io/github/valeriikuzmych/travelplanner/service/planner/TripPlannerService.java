package io.github.valeriikuzmych.travelplanner.service.planner;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;

public interface TripPlannerService {

    TripPlanDTO getPlanForTrip(Long tripId, String userEmail);

}
