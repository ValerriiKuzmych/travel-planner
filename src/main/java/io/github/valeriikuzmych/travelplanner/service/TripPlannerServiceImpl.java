package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.*;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.*;
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


        Map<LocalDate, WeatherDayDTO> weatherMap = new HashMap<>();

        try {
            Map<String, Object> raw = weatherService.getWeather(trip.getCity());

            weatherMap = convertWeather(raw, trip.getStartDate(), trip.getEndDate());

        } catch (Exception e) {

            e.printStackTrace();

        }

        dto.setWeather(weatherMap);

        dto.setActivities(convertActivities(trip));

        return dto;
    }


    private Map<LocalDate, WeatherDayDTO> convertWeather(Map<String, Object> raw,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {

        Map<LocalDate, WeatherDayDTO> result = new HashMap<>();

        if (raw == null) {
            return result;
        }

        Object listObj = raw.get("list");
        Object cityObj = raw.get("city");

        if (!(listObj instanceof List<?> rawList)) {

            return result;
        }

        int timezoneOffsetSeconds = 0;

        if (cityObj instanceof Map<?, ?> cityMap) {

            Object tz = cityMap.get("timezone");

            if (tz instanceof Number tzNumber) {

                timezoneOffsetSeconds = tzNumber.intValue();
            }
        }

        for (Object element : rawList) {

            if (!(element instanceof Map<?, ?> entry)) {
                continue;
            }


            Object dtObj = entry.get("dt");
            if (!(dtObj instanceof Number dtNumber)) {
                continue;
            }

            long dtUtcSeconds = dtNumber.longValue();

            LocalDateTime localDateTime =
                    Instant.ofEpochSecond(dtUtcSeconds)
                            .plusSeconds(timezoneOffsetSeconds)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime();

            LocalDate date = localDateTime.toLocalDate();
            LocalTime time = localDateTime.toLocalTime();

            if (date.isBefore(startDate) || date.isAfter(endDate)) {

                continue;

            }

            int hour = time.getHour();

            if (hour != 7 && hour != 13 && hour != 19) {

                continue;

            }

            Object mainObj = entry.get("main");
            Object weatherObj = entry.get("weather");

            if (!(mainObj instanceof Map<?, ?> mainMap)
                    || !(weatherObj instanceof List<?> weatherList)
                    || weatherList.isEmpty()) {

                continue;
            }


            Object tempObj = mainMap.get("temp");

            Object descObj = ((Map<?, ?>) weatherList.get(0)).get("description");


            if (!(tempObj instanceof Number number)
                    || !(descObj instanceof String description)) {
                continue;
            }

            String displayTime = String.format("%02d:00", hour);

            WeatherTimeDTO timeDTO = new WeatherTimeDTO(
                    displayTime,
                    number.doubleValue(),
                    description
            );

            result
                    .computeIfAbsent(date, d -> new WeatherDayDTO())
                    .getTimes()
                    .add(timeDTO);
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
