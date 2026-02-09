package io.github.valeriikuzmych.travelplanner.controller.api;

import io.github.valeriikuzmych.travelplanner.dto.trip.TripResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.trip.TripService;
import io.github.valeriikuzmych.travelplanner.service.weather.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {


    private final TripService tripService;

    private final UserRepository userRepository;

    private final WeatherService weatherService;


    public TripController(TripService tripService, WeatherService weatherService, UserRepository userRepository) {

        this.userRepository = userRepository;
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
    public ResponseEntity<List<TripResponse>> getTripsByUser(@PathVariable Long userId, Principal principal) {

        User requestedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        if (!requestedUser.getEmail().equals(principal.getName())) {

            throw new AccessDeniedException("Forbidden");

        }

        List<TripResponse> trips = tripService.getTripsForUser(principal.getName());

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
