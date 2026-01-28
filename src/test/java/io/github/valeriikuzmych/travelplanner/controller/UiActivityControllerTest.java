package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.ActivityService;
import io.github.valeriikuzmych.travelplanner.service.TripService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UiActivityControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private ActivityService activityService;

    @Test
    void redirectIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/trips/1/activities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void pageLoads_success() throws Exception {

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setCity("Rome");

        when(tripService.getTrip(1L, "user@mail.com"))
                .thenReturn(trip);

        mockMvc.perform(get("/trips/1/activities"))
                .andExpect(status().isOk())
                .andExpect(view().name("activities_list"))
                .andExpect(model().attributeExists("trip"))
                .andExpect(model().attributeExists("activities"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void forbiddenIfNotOwner() throws Exception {

        when(tripService.getTrip(1L, "user@mail.com"))
                .thenThrow(new AccessDeniedException("Forbidden"));

        mockMvc.perform(get("/trips/1/activities"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void notFoundIfTripDoesNotExist() throws Exception {

        when(tripService.getTrip(1L, "user@mail.com"))
                .thenThrow(new EntityNotFoundException("Trip not found"));

        mockMvc.perform(get("/trips/1/activities"))
                .andExpect(status().isNotFound());
    }


}
