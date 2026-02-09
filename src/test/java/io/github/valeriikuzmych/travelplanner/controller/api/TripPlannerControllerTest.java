package io.github.valeriikuzmych.travelplanner.controller.api;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.planner.TripPlannerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TripPlannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripPlannerService tripPlannerService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getTripPlan_success() throws Exception {

        TripPlanDTO dto = new TripPlanDTO();
        dto.setTripId(1L);
        dto.setCity("Rome");
        dto.setActivities(Map.of());
        dto.setWeather(Map.of());

        when(tripPlannerService.getPlanForTrip(eq(1L), eq("john@example.com")))
                .thenReturn(dto);

        mockMvc.perform(get("/api/trips/1/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(1L))
                .andExpect(jsonPath("$.city").value("Rome"));

        verify(tripPlannerService, times(1))
                .getPlanForTrip(1L, "john@example.com");
    }

    @Test
    void getTripPlan_requiresAuthentication() throws Exception {

        mockMvc.perform(get("/api/trips/1/plan"))
                .andExpect(status().isUnauthorized());
    }

}