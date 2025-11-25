package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    @WithMockUser
    void getTripPlan_success() throws Exception {

        TripPlanDTO dto = new TripPlanDTO();
        dto.setTripId(1L);
        dto.setCity("Rome");
        dto.setStartDate(LocalDate.of(2025, 10, 10));
        dto.setEndDate(LocalDate.of(2025, 10, 15));
        dto.setActivities(Map.of());
        dto.setWeather(Map.of());

        when(tripPlannerService.getPlanForTrip(1L)).thenReturn(dto);

        mockMvc.perform(get("/trips/1/plan")).andExpect(status().isOk()).andExpect(jsonPath("$.city").value("Rome")).andExpect(jsonPath("$.tripId").value(1L));

        verify(tripPlannerService, times(1)).getPlanForTrip(1L);
    }


}
