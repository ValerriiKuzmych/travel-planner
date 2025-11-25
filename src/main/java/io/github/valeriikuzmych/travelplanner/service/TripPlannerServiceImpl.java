package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
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

    public TripPlannerServiceImpl(TripRepository tripRepository, WeatherService weatherService) {
        this.tripRepository = tripRepository;
        this.weatherService = weatherService;
    }

    @Override
    public TripPlanDTO getPlanForTrip(Long tripId) {

        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalTrip.isEmpty()) {
            throw new IllegalArgumentException("Trip with id " + tripId + " not found");
        }

        Trip trip = optionalTrip.get();

        TripPlanDTO dto = new TripPlanDTO();

        dto.setTripId(trip.getId());
        dto.setCity(trip.getCity());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());

        Map<String, Object> weatherRaw = weatherService.getWeather(trip.getCity());
        Map<LocalDate, WeatherDayDTO> weatherMap = convertWeather(weatherRaw);
        dto.setWeather(weatherMap);

        Map<LocalDate, List<ActivityDTO>> activityMap = convertActivities(trip);
        dto.setActivities(activityMap);

        return dto;
    }


    private Map<LocalDate, WeatherDayDTO> convertWeather(Map<String, Object> raw) {

        List<Map<String, Object>> list = (List<Map<String, Object>>) raw.get("list");

        Map<LocalDate, WeatherDayDTO> result = new HashMap<>();

        for (Map<String, Object> entry : list) {

            String dateTime = (String) entry.get("dt_txt");
            LocalDate date = LocalDate.parse(dateTime.substring(0, 10));


            Map<String, Object> main = (Map<String, Object>) entry.get("main");
            Double temp = (Double) main.get("temp");

            List<Map<String, Object>> weatherArr = (List<Map<String, Object>>) entry.get("weather");
            String description = (String) weatherArr.get(0).get("description");

            WeatherDayDTO dto = new WeatherDayDTO();
            dto.setDescription(description);
            dto.setTemperature(temp);

            result.put(date, dto);


        }
        return result;
    }

    private Map<LocalDate, List<ActivityDTO>> convertActivities(Trip trip) {

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
