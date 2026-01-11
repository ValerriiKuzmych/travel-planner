package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import io.github.valeriikuzmych.travelplanner.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {


    private final TripService tripService;

    private final WeatherService weatherService;


    public TripController(TripService tripService, WeatherService weatherService) {

        this.tripService = tripService;
        this.weatherService = weatherService;

    }

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody TripForm form,
                                        Principal principal) {

        Trip created = tripService.createTrip(form, principal.getName());

        return ResponseEntity.ok(Map.of("id", created.getId()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable Long id, Principal principal) {

        Trip trip = tripService.getTrip(id, principal.getName());

        return ResponseEntity.ok(trip);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripBasicDTO>> getTripsByUser(@PathVariable Long userId, Principal principal) {

        List<TripBasicDTO> trips = tripService.getTripsForUser(principal.getName());

        return ResponseEntity.ok(trips);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable Long id, @RequestBody TripForm form,
                                        Principal principal) {

        Trip updated = tripService.updateTrip(id, form, principal.getName());

        return ResponseEntity.ok(Map.of("id", updated.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id, Principal principal) {

        tripService.deleteTrip(id, principal.getName());

        return ResponseEntity.ok().build();
    }

}
