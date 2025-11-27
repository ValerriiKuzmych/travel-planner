package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.TripForm;
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


    //TODO
//    @GetMapping("/{id}")
//    public ResponseEntity<Trip> getTrip(@PathVariable("id") Long tripId) {
//
//        try {
//            Trip trip = tripService.getTrip(tripId);
//            return ResponseEntity.ok(trip);
//
//        } catch (IllegalArgumentException e) {
//
//            return ResponseEntity.notFound().build();
//        }
//
//
//    }

    //TODO
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Trip>> getAllTrips(@PathVariable Long userId) {
//
//        List<Trip> trips = tripService.getTripsByUserId(userId);
//
//        return ResponseEntity.ok(trips);
//
//    }

    //TODO
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateTrip(@PathVariable Long id,
//                                        @RequestBody TripForm form,
//                                        Principal principal) {
//
//        Trip updated = tripService.updateTrip(id, form, principal.getName());
//
//        return ResponseEntity.ok(Map.of("id", updated.getId()));
//    }


}
