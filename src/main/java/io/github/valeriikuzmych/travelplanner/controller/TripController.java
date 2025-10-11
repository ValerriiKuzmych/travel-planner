package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.ITripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {


    private final ITripService tripService;

    private final UserRepository userRepository;

    public TripController(ITripService tripService, UserRepository userRepository) {

        this.tripService = tripService;

        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> createTrip(@RequestBody Trip trip) {

        tripService.createTrip(trip);

        return ResponseEntity.ok("Trip created successfully");

    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable("id") Long tripId) {

        try {
            Trip trip = tripService.getTrip(tripId);
            return ResponseEntity.ok(trip);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }


    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getAllTrips(@PathVariable Long userId) {

        List<Trip> trips = tripService.getTripsByUserId(userId);

        return ResponseEntity.ok(trips);

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTrip(@PathVariable("id") Long tripId, @RequestBody Trip updatedTrip) {

        try {
            tripService.updateTrip(tripId, updatedTrip);

            return ResponseEntity.ok("Trip updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteTrip(@PathVariable("id") Long tripId) {

        try {
            tripService.deleteTrip(tripId);

            return ResponseEntity.ok("Trip deleted successfully");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


}
