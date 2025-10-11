package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.LoginRequest;
import io.github.valeriikuzmych.travelplanner.dto.RegistrationRequest;
import io.github.valeriikuzmych.travelplanner.service.IUserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        boolean isAuthenticate = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (isAuthenticate) {

            return ResponseEntity.ok("Login successful");
            
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }


    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {

        userService.registerUser(registrationRequest.getEmail(), registrationRequest.getPassword());

        return ResponseEntity.ok("User registered successfully.");

    }


}
