package io.github.valeriikuzmych.travelplanner.controller.ui;

import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UiTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;


    @Test
    void tripsPage_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/trips")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }


    @Test
    void tripsPage_authenticated_rendersView() throws Exception {
        when(tripService.getTripsForUser("user@test.com"))
                .thenReturn(List.of(new TripBasicDTO()));

        mockMvc.perform(get("/trips")
                        .with(user("user@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("trips"))
                .andExpect(model().attributeExists("trips"));
    }


    @Test
    void createTrip_post_redirects() throws Exception {
        mockMvc.perform(post("/trips/create")
                        .with(user("user@test.com"))
                        .with(csrf())
                        .param("city", "Paris")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips"));

        verify(tripService).createTrip(any(TripForm.class), eq("user@test.com"));
    }


    @Test
    void editTripForm_renders() throws Exception {
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setCity("Rome");

        when(tripService.getTrip(1L, "user@test.com")).thenReturn(trip);

        mockMvc.perform(get("/trips/1/edit")
                        .with(user("user@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_trip"))
                .andExpect(model().attributeExists("form"));
    }


    @Test
    void updateTrip_post_redirects() throws Exception {
        mockMvc.perform(post("/trips/1/edit")
                        .with(user("user@test.com"))
                        .with(csrf())
                        .param("city", "London")
                        .param("startDate", "2025-05-01")
                        .param("endDate", "2025-05-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/1"));

        verify(tripService).updateTrip(eq(1L), any(TripForm.class), eq("user@test.com"));
    }


    @Test
    void deleteTrip_redirects() throws Exception {
        mockMvc.perform(post("/trips/1/delete")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips"));

        verify(tripService).deleteTrip(1L, "user@test.com");
    }


    @Test
    void tripDetails_renders() throws Exception {
        TripDetailsDTO dto = new TripDetailsDTO();
        dto.setId(1L);

        when(tripService.getTripDetails(1L, "user@test.com")).thenReturn(dto);

        mockMvc.perform(get("/trips/1")
                        .with(user("user@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("trip_details"))
                .andExpect(model().attributeExists("trip"));
    }

    @Test
    void createTripForm_renders() throws Exception {
        mockMvc.perform(get("/trips/create")
                        .with(user("user@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("create_trip"))
                .andExpect(model().attributeExists("trip"));
    }

    @Test
    void trips_redirectIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/trips")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}