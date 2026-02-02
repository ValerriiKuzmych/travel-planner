package io.github.valeriikuzmych.travelplanner.controller.rest;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/trips")
public class TripPlannerController {


    private final TripPlannerService tripPlannerService;

    public TripPlannerController(TripPlannerService tripPlannerService) {

        this.tripPlannerService = tripPlannerService;

    }

    @GetMapping("/{id}/plan")
    public ResponseEntity<TripPlanDTO> getTripPlan(@PathVariable Long id, Principal principal) {

        return ResponseEntity.ok(tripPlannerService.getPlanForTrip(id, principal.getName()));

    }
}
