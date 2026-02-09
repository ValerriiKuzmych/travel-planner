package io.github.valeriikuzmych.travelplanner.service.user;

import io.github.valeriikuzmych.travelplanner.dto.RegistrationRequest;

public interface UserService {

    void registerUser(String email, String password);

    void registerUser(RegistrationRequest request);

    boolean authenticate(String email, String password);
}
