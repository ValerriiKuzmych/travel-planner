package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.ActivityService;
import io.github.valeriikuzmych.travelplanner.service.TripServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UiActivityControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TripServiceImpl tripServiceImpl;

    @MockitoBean
    private ActivityService activityService;

    @Test
    void redirectIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/trips/1/activities"))
                .andExpect(status().isUnauthorized()); // проверяем 401
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = {"USER"})
    void pageLoads_success() throws Exception {

        Trip mockTrip = new Trip();
        mockTrip.setId(1L);
        mockTrip.setCity("Paris");

        when(tripServiceImpl.getTrip(1L, "user@mail.com")).thenReturn(mockTrip);

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(10L);
        activityDTO.setName("Sauna");
        activityDTO.setNote("Relax after walking");
        activityDTO.setDate(java.time.LocalDate.of(2026, 12, 15));
        activityDTO.setStartTime(java.time.LocalTime.of(18, 0));
        activityDTO.setEndTime(java.time.LocalTime.of(19, 0));

        when(activityService.getActivitiesByTripForUser(1L, "user@mail.com"))
                .thenReturn(List.of(convertToActivity(activityDTO)));

        mockMvc.perform(get("/trips/1/activities").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("activities_list"))
                .andExpect(model().attributeExists("trip"))
                .andExpect(model().attributeExists("activities"));
    }


    private io.github.valeriikuzmych.travelplanner.entity.Activity convertToActivity(ActivityDTO dto) {
        io.github.valeriikuzmych.travelplanner.entity.Activity activity =
                new io.github.valeriikuzmych.travelplanner.entity.Activity();
        activity.setId(dto.getId());
        activity.setName(dto.getName());
        activity.setDate(dto.getDate());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        return activity;
    }

}
