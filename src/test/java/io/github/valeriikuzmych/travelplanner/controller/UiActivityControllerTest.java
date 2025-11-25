package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


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

    @Test
    void redirectIfNotAuthenticated() throws Exception {

        Trip mockTrip = new Trip();

        when(tripServiceImpl.getTrip(1L)).thenReturn(mockTrip);

        mockMvc.perform(get("/trips/1/activities")
                        .with(csrf())
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void pageLoads() throws Exception {

        Trip mockTrip = new Trip();

        when(tripServiceImpl.getTrip(1L)).thenReturn(mockTrip);

        mockMvc.perform(get("/trips/1/activities").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("activities_list"))
                .andExpect(model().attributeExists("activities"));
    }


}
