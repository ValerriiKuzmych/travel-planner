package io.github.valeriikuzmych.travelplanner.service.planner;

import io.github.valeriikuzmych.travelplanner.dto.*;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.DayPeriod;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherDayResponse;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherPeriodResponse;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherTimeResponse;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.service.validator.OwnershipValidator;
import io.github.valeriikuzmych.travelplanner.service.weather.WeatherService;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

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


        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        validator.assertUserOwnTrip(trip, userEmail);


        TripPlanDTO dto = new TripPlanDTO();

        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setTripDates(generateTripDates(dto));

        Map<String, Object> raw = weatherService.getWeather(trip.getCity());

        Map<LocalDate, WeatherDayResponse> convertedWeather = convertWeather(raw);

        int offset = 0;
        if (raw.get("city") instanceof Map<?, ?> city && city.get("timezone") instanceof Number tz) {
            offset = tz.intValue();
        }
        ZoneId cityZone = ZoneOffset.ofTotalSeconds(offset);

        Map<LocalDate, WeatherDayResponse> filteredWeather =
                filterWeatherForTrip(
                        convertedWeather,
                        trip.getStartDate(),
                        trip.getEndDate(),
                        cityZone,
                        dto
                );

        dto.setWeather(filteredWeather);

        dto.setActivities(convertActivities(trip));


        return dto;
    }


    private Map<LocalDate, WeatherDayResponse> convertWeather(Map<String, Object> raw) {


        Map<LocalDate, WeatherDayResponse> result = new LinkedHashMap<>();

        if (raw == null || !(raw.get("list") instanceof List<?> list)) {
            return result;
        }

        int timezoneOffsetSeconds = 0;

        if (raw.get("city") instanceof Map<?, ?> city && city.get("timezone") instanceof Number tz) {
            timezoneOffsetSeconds = tz.intValue();
        }

        Map<LocalDate, Map<DayPeriod, List<WeatherTimeResponse>>> buffer = new LinkedHashMap<>();

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

            WeatherTimeResponse timeDTO = new WeatherTimeResponse(
                    time.toString(),
                    Math.round(temp.doubleValue() * 10.0) / 10.0,
                    (String) desc
            );

            DayPeriod period = DayPeriod.from(time);

            buffer
                    .computeIfAbsent(date, d -> new EnumMap<>(DayPeriod.class))
                    .computeIfAbsent(period, p -> new ArrayList<>())
                    .add(timeDTO);
        }

        for (var dateEntry : buffer.entrySet()) {

            WeatherDayResponse dayDTO = new WeatherDayResponse();

            for (var periodEntry : dateEntry.getValue().entrySet()) {

                List<WeatherTimeResponse> times = periodEntry.getValue();

                double avgTemp = times.stream()
                        .mapToDouble(WeatherTimeResponse::getTemperature)
                        .average()
                        .orElse(0);

                String description = times.get(0).getDescription();

                dayDTO.getPeriods().add(
                        new WeatherPeriodResponse(
                                periodEntry.getKey(),
                                Math.round(avgTemp * 10.0) / 10.0,
                                description
                        )
                );
            }

            result.put(dateEntry.getKey(), dayDTO);
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
            dto.setNote(act.getNote());
            dto.setDate(act.getDate());
            dto.setStartTime(act.getStartTime());


            result.computeIfAbsent(act.getDate(), d -> new ArrayList<>()).add(dto);


        }

        return result;
    }

    private Map<LocalDate, WeatherDayResponse> filterWeatherForTrip(
            Map<LocalDate, WeatherDayResponse> rawWeather,
            LocalDate tripStart,
            LocalDate tripEnd,
            ZoneId cityZone,
            TripPlanDTO dto
    ) {
        if (rawWeather.isEmpty()) {
            dto.setWeatherLimited(true);
            return Collections.emptyMap();
        }

        LocalDate today = LocalDate.now(cityZone);


        LocalDate displayFrom =
                today.isAfter(tripStart) ? today : tripStart;


        LocalDate apiLastDate =
                rawWeather.keySet().stream()
                        .max(LocalDate::compareTo)
                        .orElse(null);

        if (apiLastDate == null || apiLastDate.isBefore(displayFrom)) {
            dto.setWeatherLimited(true);
            return Collections.emptyMap();
        }


        LocalDate displayTo =
                apiLastDate.isBefore(tripEnd) ? apiLastDate : tripEnd;


        dto.setWeatherLimited(apiLastDate.isBefore(tripEnd));

        Map<LocalDate, WeatherDayResponse> result = new LinkedHashMap<>();

        for (Map.Entry<LocalDate, WeatherDayResponse> entry : rawWeather.entrySet()) {
            LocalDate date = entry.getKey();

            if (!date.isBefore(displayFrom) && !date.isAfter(displayTo)) {
                result.put(date, entry.getValue());
            }
        }

        return result;
    }

    private List<LocalDate> generateTripDates(TripPlanDTO plan) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = plan.getStartDate();
        while (!date.isAfter(plan.getEndDate())) {
            dates.add(date);
            date = date.plusDays(1);
        }
        return dates;
    }
}
