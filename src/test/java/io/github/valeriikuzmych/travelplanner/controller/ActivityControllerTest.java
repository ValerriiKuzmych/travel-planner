package io.github.valeriikuzmych.travelplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ActivityControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    TripRepository tripRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Trip testTrip;
    private Activity testActivity;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("testactivityuser@example.com");
        testUser.setPassword("testuser123");
        testUser.setRole("USER");

        userRepository.save(testUser);

        testTrip = new Trip();
        testTrip.setUser(testUser);
        testTrip.setCity("Rome");
        testTrip.setStartDate(LocalDate.of(2026, 10, 10));
        testTrip.setEndDate(LocalDate.of(2026, 10, 15));

        tripRepository.save(testTrip);

        testActivity = new Activity();
        testActivity.setType("Sport");
        testActivity.setName("Tennis");
        testActivity.setDate(LocalDate.of(2026, 10, 11));
        testActivity.setStartTime(LocalTime.of(14, 00));
        testActivity.setEndTime(LocalTime.of(15, 00));
        testActivity.setTrip(testTrip);
        activityRepository.save(testActivity);


    }


    @Test
    @WithMockUser(username = "testcreateactivityuser@example.com", roles = {"USER"})
    void createActivity_success() throws Exception {


        User createTestUser = new User();
        createTestUser.setEmail("testcreateactivityuser@example.com");
        createTestUser.setPassword("testuser12345");
        createTestUser.setRole("USER");

        userRepository.save(createTestUser);

        Trip createTestTrip = new Trip();
        createTestTrip.setUser(createTestUser);
        createTestTrip.setCity("Rome");
        createTestTrip.setStartDate(LocalDate.of(2026, 10, 10));
        createTestTrip.setEndDate(LocalDate.of(2026, 10, 15));

        tripRepository.save(createTestTrip);

        Activity createTestActivity = new Activity();
        createTestActivity.setType("Sport");
        createTestActivity.setName("Tennis");
        createTestActivity.setDate(LocalDate.of(2026, 10, 11));
        createTestActivity.setStartTime(LocalTime.of(14, 00));
        createTestActivity.setEndTime(LocalTime.of(15, 00));
        createTestActivity.setTrip(createTestTrip);

        String activityJson = objectMapper.writeValueAsString(createTestActivity);

        mockMvc.perform(post("/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityJson))
                .andExpect(status().isOk());

        var activities = activityRepository.findByTripId(createTestTrip.getId());
        assertThat(activities).hasSize(1);

        Activity savedActivity = activities.get(0);

        assertThat(savedActivity.getType()).isEqualTo("Sport");
        assertThat(savedActivity.getName()).isEqualTo("Tennis");
        assertThat(savedActivity.getTrip().getUser().getId()).isEqualTo(createTestUser.getId());
        assertThat(savedActivity.getTrip().getId()).isEqualTo(createTestTrip.getId());
        assertThat(savedActivity.getDate()).isEqualTo(LocalDate.of(2026, 10, 11));
        assertThat(savedActivity.getStartTime()).isEqualTo(LocalTime.of(14, 00));
        assertThat(savedActivity.getEndTime()).isEqualTo(LocalTime.of(15, 00));
        assertThat(savedActivity.getStartTime().isBefore(savedActivity.getEndTime()));


    }


}
