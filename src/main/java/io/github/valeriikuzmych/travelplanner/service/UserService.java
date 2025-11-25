package io.github.valeriikuzmych.travelplanner.service;

public interface UserService {

    void registerUser(String email, String password);

    boolean authenticate(String email, String password);
}
