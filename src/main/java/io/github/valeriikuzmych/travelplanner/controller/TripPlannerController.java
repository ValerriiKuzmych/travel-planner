package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.ITripPlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripPlannerController {


    private final ITripPlannerService tripPlannerService;

    public TripPlannerController(ITripPlannerService tripPlannerService) {

        this.tripPlannerService = tripPlannerService;

    }

    @GetMapping("/{id}/plan")
    public ResponseEntity<TripPlanDTO> getTripPlan(@PathVariable Long id) {

        TripPlanDTO plan = tripPlannerService.getPlanForTrip(id);

        return ResponseEntity.ok(plan);

    }
}
