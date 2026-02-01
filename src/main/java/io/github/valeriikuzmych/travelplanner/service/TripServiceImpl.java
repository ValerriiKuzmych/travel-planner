package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.*;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
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

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return tripRepository.findByUserId(user.getId()).stream().map(this::mapToBasicDTO).toList();

    }

    @Override
    public Trip createTrip(TripForm form, String email) {

        validateTripForm(form);

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

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));


        return trip;

    }


    @Override
    public Trip updateTrip(Long id, TripForm form, String email) {


        validator.assertUserOwnTrip(id, email);

        validateTripForm(form);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        trip.setCity(form.getCity());
        trip.setStartDate(form.getStartDate());
        trip.setEndDate(form.getEndDate());

        return tripRepository.save(trip);

    }

    @Override
    public TripDetailsDTO getTripDetails(Long id, String email) {

        Trip trip = getTrip(id, email);

        TripDetailsDTO dto = mapToDetailsDTO(trip);

        dto.setEditable(true);

        return dto;
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

    private void validateTripForm(TripForm form) {

        if (form == null) throw new IllegalArgumentException("Trip form must not be null");

        if (form.getCity() == null || form.getCity().isBlank())

            throw new IllegalArgumentException("City must not be empty");

        if (form.getStartDate() == null || form.getEndDate() == null)

            throw new IllegalArgumentException("Start date and end date must not be null");

        if (form.getStartDate().isAfter(form.getEndDate()))

            throw new IllegalArgumentException("Start date cannot be after end date");
    }


    private TripBasicDTO mapToBasicDTO(Trip trip) {

        TripBasicDTO dto = new TripBasicDTO();

        dto.setId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setActivities(trip.getActivities().stream().map(this::mapActivity).toList());

        return dto;
    }

    private TripDetailsDTO mapToDetailsDTO(Trip trip) {

        TripDetailsDTO dto = new TripDetailsDTO();

        dto.setId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setActivitiesByDate(groupActivitiesByDate(trip.getActivities()));

        return dto;
    }

    private TripPlanDTO mapToPlanDTO(Trip trip) {

        TripPlanDTO dto = new TripPlanDTO();
        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setActivities(groupActivitiesByDate(trip.getActivities()));

        return dto;
    }

    private ActivityDTO mapActivity(Activity activity) {

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setNote(activity.getNote());
        dto.setDate(activity.getDate());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());

        return dto;
    }

    private Map<LocalDate, List<ActivityDTO>> groupActivitiesByDate(List<Activity> activities) {

        Map<LocalDate, List<ActivityDTO>> grouped = new HashMap<>();

        for (Activity a : activities) {

            grouped.computeIfAbsent(a.getDate(), d -> new ArrayList<>()).add(mapActivity(a));
        }

        return grouped;
    }


}