package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.ActivityResponseDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TripPlannerServiceImpl implements TripPlannerService {

    private final TripRepository tripRepository;

    private final WeatherService weatherService;

    private final OwnershipValidator validator;

    public TripPlannerServiceImpl(TripRepository tripRepository, WeatherService weatherService,
                                  OwnershipValidator validator) {

        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
        this.validator = validator;
    }

    @Override
    public TripPlanDTO getPlanForTrip(Long tripId, String userEmail) {

        validator.assertUserOwnTrip(tripId, userEmail);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trip with id " + tripId + " not found"));


        TripPlanDTO dto = new TripPlanDTO();

        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());


        Map<LocalDate, WeatherDayDTO> weatherMap = Collections.emptyMap();

        try {
            Map<String, Object> raw = weatherService.getWeather(trip.getCity());
            weatherMap = convertWeather(raw);
        } catch (Exception ignored) {

        }

        dto.setWeather(weatherMap);

        dto.setActivities(convertActivities(trip));

        return dto;
    }


    private Map<LocalDate, WeatherDayDTO> convertWeather(Map<String, Object> raw) {

        if (raw == null) {
            return new HashMap<>();
        }

        Object listObj = raw.get("list");

        if (!(listObj instanceof List<?>)) {

            return new HashMap<>();
        }

        List<?> rawList = (List<?>) listObj;

        Map<LocalDate, WeatherDayDTO> result = new HashMap<>();

        for (Object element : rawList) {

            if (!(element instanceof Map<?, ?> entry)) {
                continue;
            }


            Object dtObj = entry.get("dt_txt");

            if (!(dtObj instanceof String dtTxt) || dtTxt.length() < 10) {
                continue;
            }

            LocalDate date = LocalDate.parse(dtTxt.substring(0, 10));


            double temp = 0.0;

            Object mainObj = entry.get("main");

            if (mainObj instanceof Map<?, ?> mainMap) {

                Object tempObj = mainMap.get("temp");

                if (tempObj instanceof Number number) {

                    temp = number.doubleValue();
                }
            }


            String description = "unknown";

            Object weatherObj = entry.get("weather");

            if (weatherObj instanceof List<?> weatherList && !weatherList.isEmpty()) {

                Object item0 = weatherList.get(0);

                if (item0 instanceof Map<?, ?> wMap) {

                    Object descObj = wMap.get("description");

                    if (descObj instanceof String text) {

                        description = text;
                    }
                }
            }

            WeatherDayDTO dto = new WeatherDayDTO();
            dto.setTemperature(temp);
            dto.setDescription(description);

            result.put(date, dto);
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


            result.computeIfAbsent(act.getDate(), d -> new ArrayList<>())
                    .add(dto);


        }

        return result;
    }
}
