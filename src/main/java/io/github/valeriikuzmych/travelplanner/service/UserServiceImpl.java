package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.RegistrationRequest;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.UserRegistrationException;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(String email, String rawPassword) {

        if (userRepository.existsByEmail(email)) {
            throw new UserRegistrationException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");

        userRepository.save(user);
    }

    @Override
    public void registerUser(RegistrationRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserRegistrationException("Passwords do not match");
        }

        registerUser(request.getEmail(), request.getPassword());

    }


    @Override
    public boolean authenticate(String email, String rawPassword) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        return passwordEncoder.matches(rawPassword, user.getPassword());

    }
}
