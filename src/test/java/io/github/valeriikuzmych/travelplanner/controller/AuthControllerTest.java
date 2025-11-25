package io.github.valeriikuzmych.travelplanner.controller;


import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @Test
    void registerUser_success() throws Exception {

        mockMvc.perform(post("/auth/registration").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"t3@t.com\",\"password\":\"pass\"}"))
                .andExpect(status().isOk());

        assertTrue(userRepository.existsByEmail("t3@t.com"));
    }

    @Test
    void loginUser_success() throws Exception {

        userService.registerUser("t2@t.com", "pass");

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"t2@t.com\",\"password\":\"pass\"}"))
                .andExpect(status().isOk());

    }


}
