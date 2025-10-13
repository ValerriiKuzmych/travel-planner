package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService implements ITripService {


    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
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
