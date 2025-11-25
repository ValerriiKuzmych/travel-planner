package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import io.github.valeriikuzmych.travelplanner.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{tripId}/weather")
    public ResponseEntity<?> getTripWeather(@PathVariable Long tripId) {

        Trip trip = tripService.getTrip(tripId);

        Map<String, Object> weather = weatherService.getWeather(trip.getCity());


        return ResponseEntity.ok(weather);

    }


}
