package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherTimeDTO;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerService;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UiTripPlanController.class)
class UiTripPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripPlannerService tripPlanService;

    private TripPlanDTO planDTO;

    @BeforeEach
    void setup() {
        planDTO = new TripPlanDTO();

        planDTO.setTripId(1L);
        planDTO.setCity("Paris");
        planDTO.setStartDate(LocalDate.of(2025, 3, 10));
        planDTO.setEndDate(LocalDate.of(2025, 3, 20));


        WeatherDayDTO weatherDay = new WeatherDayDTO();

        weatherDay.getTimes().add(
                new WeatherTimeDTO("07:00", 14.0, "rain")
        );
        weatherDay.getTimes().add(
                new WeatherTimeDTO("13:00", 18.0, "sunny")
        );
        weatherDay.getTimes().add(
                new WeatherTimeDTO("19:00", 16.0, "cloudy")
        );

        Map<LocalDate, WeatherDayDTO> weatherMap = new HashMap<>();

        weatherMap.put(LocalDate.of(2025, 3, 11), weatherDay);
        planDTO.setWeather(weatherMap);

        ActivityDTO act = new ActivityDTO();

        act.setId(100L);
        act.setName("Museum Tour");
        act.setDate(LocalDate.of(2025, 3, 11));
        act.setStartTime(LocalTime.of(10, 0));

        Map<LocalDate, List<ActivityDTO>> activities = new HashMap<>();

        activities.put(LocalDate.of(2025, 3, 11), List.of(act));
        planDTO.setActivities(activities);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void showTripPlan_success() throws Exception {

        Mockito.when(tripPlanService.getPlanForTrip(anyLong(), anyString()))
                .thenReturn(planDTO);

        mockMvc.perform(get("/trips/1/plan"))
                .andExpect(status().isOk())
                .andExpect(view().name("trip-plan"))
                .andExpect(model().attributeExists("plan"))

                .andExpect(model().attribute("plan",
                        hasProperty("city", equalTo("Paris"))))
                .andExpect(model().attribute("plan",
                        hasProperty("weather")))
                .andExpect(model().attribute("plan",
                        hasProperty("activities")));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void showTripPlan_notFound() throws Exception {

        Mockito.when(tripPlanService.getPlanForTrip(anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Trip not found"));

        mockMvc.perform(get("/trips/9999/plan"))
                .andExpect(status().isBadRequest()); // thrown by controller
    }

}
