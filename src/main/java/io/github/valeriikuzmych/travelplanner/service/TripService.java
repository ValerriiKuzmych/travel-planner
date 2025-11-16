package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService implements ITripService {


    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Trip> getTripsForUser(String emailOrUsername) {

        return tripRepository.findAllByUserEmail(emailOrUsername);

    }

    @Override
    public void createTrip(Trip trip) {

        if (trip.getId() != null) {
            throw new IllegalArgumentException("New trip must not have an ID");
        }

        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        } else {

            tripRepository.save(trip);
        }


    }

    @Override
    public void createTripForUser(String email, Trip trip) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            throw new IllegalArgumentException("User with email: " + email + "not found");
        }

        User user = optionalUser.get();

        trip.setUser(user);
        createTrip(trip);

    }

    @Override
    public Trip getTrip(Long id) {

        Optional<Trip> optionalTrip = tripRepository.findById(id);

        if (optionalTrip.isEmpty()) {
            throw new IllegalArgumentException("Trip with id " + id + " not found");
        }

        return optionalTrip.get();
    }

    @Override
    public List<Trip> getTripsByUserId(Long userId) {

        return tripRepository.findByUserId(userId);
    }

    @Override
    public void updateTrip(Long id, Trip updatedTrip) {

        Trip trip = getTrip(id);

        if (updatedTrip.getStartDate().isAfter(updatedTrip.getEndDate())) {

            throw new IllegalArgumentException("Start time cannot be after end time");

        } else {


            trip.setCity(updatedTrip.getCity());
            trip.setStartDate(updatedTrip.getStartDate());
            trip.setEndDate(updatedTrip.getEndDate());

            tripRepository.save(trip);


        }


    }

    @Override
    public void deleteTrip(Long id) {

        if (!tripRepository.existsById(id)) {

            throw new IllegalArgumentException("Trip with id " + id + "not found.");

        } else {

            tripRepository.deleteById(id);

        }


    }


}
