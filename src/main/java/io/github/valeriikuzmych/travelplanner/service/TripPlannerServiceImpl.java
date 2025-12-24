package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.*;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripPlannerServiceImpl implements TripPlannerService {

    private final TripRepository tripRepository;

    private final WeatherService weatherService;

    private final OwnershipValidator validator;

    public TripPlannerServiceImpl(TripRepository tripRepository, WeatherService weatherService, OwnershipValidator validator) {

        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
        this.validator = validator;
    }

    @Override
    public TripPlanDTO getPlanForTrip(Long tripId, String userEmail) {

        validator.assertUserOwnTrip(tripId, userEmail);

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("Trip with id " + tripId + " not found"));


        TripPlanDTO dto = new TripPlanDTO();

        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        Map<String, Object> raw = weatherService.getWeather(trip.getCity());
        System.out.println("RAW WEATHER: " + raw);
        dto.setWeather(convertWeather(raw));

        dto.setActivities(convertActivities(trip));

        System.out.println("Weather size = " + dto.getWeather().size());
        return dto;
    }


    private Map<LocalDate, WeatherDayDTO> convertWeather(Map<String, Object> raw) {
        Map<LocalDate, WeatherDayDTO> result = new LinkedHashMap<>();

        if (raw == null || !(raw.get("list") instanceof List<?> list)) {
            return result;
        }

        int timezoneOffsetSeconds = 0;
        if (raw.get("city") instanceof Map<?, ?> city && city.get("timezone") instanceof Number tz) {
            timezoneOffsetSeconds = tz.intValue();
        }

        for (Object item : list) {
            if (!(item instanceof Map<?, ?> entry)) continue;
            if (!(entry.get("dt") instanceof Number dt)) continue;

            Instant instant = Instant.ofEpochSecond(dt.longValue());
            LocalDate date = instant.atZone(ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds)).toLocalDate();
            LocalTime time = instant.atZone(ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds)).toLocalTime();

            if (!(entry.get("main") instanceof Map<?, ?> main)) continue;
            if (!(entry.get("weather") instanceof List<?> weatherList) || weatherList.isEmpty()) continue;

            Object tempObj = main.get("temp");
            Object descObj = ((Map<?, ?>) weatherList.get(0)).get("description");

            if (!(tempObj instanceof Number temp) || !(descObj instanceof String desc)) continue;

            WeatherTimeDTO dto = new WeatherTimeDTO(
                    time.toString(),
                    Math.round(temp.doubleValue() * 10.0) / 10.0,
                    (String) desc
            );

            result.computeIfAbsent(date, d -> new WeatherDayDTO()).getTimes().add(dto);
        }

        return result;
    }


    private Map<LocalDate, List<ActivityDTO>> convertActivities(Trip trip) {

        if (trip.getActivities() == null || trip.getActivities().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<LocalDate, List<ActivityDTO>> result = new HashMap<>();

        for (Activity act : trip.getActivities()) {

            ActivityDTO dto = new ActivityDTO();

            dto.setId(act.getId());
            dto.setName(act.getName());
            dto.setDate(act.getDate());
            dto.setStartTime(act.getStartTime());


            result.computeIfAbsent(act.getDate(), d -> new ArrayList<>()).add(dto);


        }

        return result;
    }
}
