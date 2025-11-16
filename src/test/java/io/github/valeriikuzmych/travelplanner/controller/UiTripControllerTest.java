package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UiTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TripService tripService;

    @MockitoBean
    UserRepository userRepository;

    @Test
    void tripsShouldRedirectToLogin() throws Exception {

        mockMvc.perform(get("/trips")
                        .with(csrf())
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));


    }

    @Test
    void tripsShouldReturnView() throws Exception {

        List<Trip> mockTrips = List.of(new Trip(), new Trip());

        when(tripService.getTripsForUser("user@test.com"))
                .thenReturn(mockTrips);

        mockMvc.perform(get("/trips")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("trips"))
                .andExpect(model().attributeExists("trips"));

    }

    @Test
    void createTripFormShouldRenderTemplate() throws Exception {

        mockMvc.perform(get("/trips/create")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create_trip"))
                .andExpect(model().attributeExists("trip"));
    }

    @Test
    void createTripShouldRedirect() throws Exception {

        mockMvc.perform(post("/trips/create")
                        .param("city", "Paris")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-05")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips"));

        verify(tripService).createTripForUser(eq("user@test.com"), any(Trip.class));
    }


}
