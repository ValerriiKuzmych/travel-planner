package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService implements ITripService {

    @Autowired
    TripRepository tripRepository;

    @Override
    public void createTrip(Trip trip) {

        if (tripRepository.existsById(trip.getId())) {
            throw new IllegalArgumentException("Trip already exists");
        } else {

            tripRepository.save(trip);
        }


    }


    public Trip getTrip(Long id) {

        Optional<Trip> optionalTrip = tripRepository.findById(id);

        if (optionalTrip.isEmpty()) {
            throw new IllegalArgumentException("Trip with id " + id + " not found");
        }

        return optionalTrip.get();
    }

    @Override
    public List<Trip> getTripsByUser(Long userId) {

        return tripRepository.findByUserId(userId);
    }

    @Override
    public void updateTrip(Long id, Trip updatedTrip) {

        if (!tripRepository.existsById(id)) {

            throw new IllegalArgumentException("Trip with " + id + " not exists.");
        } else {

            Trip trip = getTrip(id);
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
