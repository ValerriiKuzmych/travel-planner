package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {


    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    private final OwnershipValidator validator;

    public TripServiceImpl(TripRepository tripRepository, UserRepository userRepository, OwnershipValidator validator) {

        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public List<Trip> getTripsForUser(String email) {

        return tripRepository.findAllByUserEmail(email);

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
    public TripDetailsDTO getTripDetailsForUser(Long tripId, String email) {

        validator.assertUserOwnTrip(tripId, email);

        return convertTripToTripDetails(tripId);
    }

    @Override
    public Trip getTripForUser(Long id, String email) {

        validator.assertUserOwnTrip(id, email);

        return getTrip(id);
    }

    @Override
    public void updateTripForUser(Long id, Trip updatedTrip, String email) {

        validator.assertUserOwnTrip(id, email);

        updateTrip(id, updatedTrip);

    }

    @Override
    public void deleteTripForUser(Long id, String email) {

        validator.assertUserOwnTrip(id, email);

        deleteTrip(id);

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
    public Trip getTrip(Long id) {

        Optional<Trip> optionalTrip = tripRepository.findById(id);

        if (optionalTrip.isEmpty()) {
            throw new IllegalArgumentException("Trip with id " + id + " not found");
        }

        return optionalTrip.get();
    }


    private TripDetailsDTO convertTripToTripDetails(Long id) {

        Trip trip = getTrip(id);

        TripDetailsDTO dto = new TripDetailsDTO();

        dto.setId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        Map<LocalDate, List<ActivityDTO>> grouped = trip.getActivities()
                .stream()
                .sorted(Comparator.comparing(Activity::getDate)
                        .thenComparing(Activity::getStartTime))
                .map(a -> {

                    ActivityDTO d = new ActivityDTO();
                    d.setId(a.getId());
                    d.setName(a.getName());
                    d.setDate(a.getDate());
                    d.setStartTime(a.getStartTime());
                    d.setEndTime(a.getEndTime());

                    return d;

                }).collect(Collectors.groupingBy(ActivityDTO::getDate));

        dto.setActivitiesByDate(grouped);
        dto.setEditable(true);

        return dto;
    }
}
