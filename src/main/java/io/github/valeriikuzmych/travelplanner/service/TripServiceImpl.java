package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.*;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


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
    public List<TripBasicDTO> getTripsForUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return tripRepository.findByUserId(user.getId()).stream()
                .map(this::mapToBasicDTO)
                .toList();
    }


    @Override
    public Trip createTrip(TripForm form, String email) {

        validateDates(form.getStartDate(), form.getEndDate());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Trip trip = new Trip();
        trip.setCity(form.getCity());
        trip.setStartDate(form.getStartDate());
        trip.setEndDate(form.getEndDate());
        trip.setUser(user);

        return tripRepository.save(trip);
    }


    @Override
    public Trip getTrip(Long id, String email) {

        validator.assertUserOwnTrip(id, email);

        return tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

    }

    @Override
    public Trip updateTrip(Long id, TripForm form, String email) {

        validator.assertUserOwnTrip(id, email);

        validateDates(form.getStartDate(), form.getEndDate());

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        trip.setCity(form.getCity());
        trip.setStartDate(form.getStartDate());
        trip.setEndDate(form.getEndDate());

        return tripRepository.save(trip);
    }


    @Override
    public TripDetailsDTO getTripDetails(Long id, String email) {

        Trip trip = getTrip(id, email);

        return mapToDetailsDTO(trip);
    }

    @Override
    public TripPlanDTO getTripPlan(Long id, String email) {

        Trip trip = getTrip(id, email);

        return mapToPlanDTO(trip);
    }

    @Override
    public void deleteTrip(Long id, String email) {

        validator.assertUserOwnTrip(id, email);

        tripRepository.deleteById(id);
    }


    private void validateDates(LocalDate start, LocalDate end) {

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

    }

    private TripBasicDTO mapToBasicDTO(Trip trip) {

        TripBasicDTO dto = new TripBasicDTO();
        dto.setId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        dto.setActivities(
                trip.getActivities().stream()
                        .map(this::mapActivity)
                        .toList()
        );

        return dto;
    }

    private TripDetailsDTO mapToDetailsDTO(Trip trip) {

        TripDetailsDTO dto = new TripDetailsDTO();
        dto.setId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        Map<LocalDate, List<ActivityDTO>> grouped = new HashMap<>();
        for (Activity a : trip.getActivities()) {
            grouped.computeIfAbsent(a.getDate(),
                    d -> new ArrayList<>()).add(mapActivity(a));
        }

        dto.setActivitiesByDate(grouped);
        dto.setEditable(true);

        return dto;
    }

    private TripPlanDTO mapToPlanDTO(Trip trip) {

        TripPlanDTO dto = new TripPlanDTO();
        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        Map<LocalDate, List<ActivityDTO>> grouped = new HashMap<>();
        for (Activity a : trip.getActivities()) {
            grouped.computeIfAbsent(a.getDate(),
                    d -> new ArrayList<>()).add(mapActivity(a));
        }

        dto.setActivities(grouped);

        return dto;
    }

    private ActivityDTO mapActivity(Activity activity) {

        ActivityDTO dto = new ActivityDTO();

        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setDate(activity.getDate());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());

        return dto;
    }

}
