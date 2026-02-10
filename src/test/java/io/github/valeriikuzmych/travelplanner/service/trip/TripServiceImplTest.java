package io.github.valeriikuzmych.travelplanner.service.trip;

import io.github.valeriikuzmych.travelplanner.dto.trip.TripResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.validator.OwnershipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripServiceImpl tripServiceImpl;


    private Trip trip;

    @BeforeEach
    void init() {

        trip = new Trip();
        trip.setId(1L);
        trip.setStartDate(LocalDate.of(2026, 12, 10));
        trip.setEndDate(LocalDate.of(2026, 12, 20));
    }

    @Test
    void createTrip_success() {

        TripForm form = new TripForm();
        form.setCity("Rome");
        form.setStartDate(LocalDate.of(2026, 10, 10));
        form.setEndDate(LocalDate.of(2026, 10, 15));

        User user = new User();
        user.setId(10L);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        Trip saved = new Trip();
        saved.setId(1L);
        saved.setCity("Rome");
        when(tripRepository.save(any(Trip.class))).thenReturn(saved);

        Trip result = tripServiceImpl.createTrip(form, "test@mail.com");

        assertNotNull(result);
        verify(tripRepository, times(1)).save(any(Trip.class));
        verify(userRepository, times(1)).findByEmail("test@mail.com");
    }


    @Test
    void createTrip_invalidDates_throwsException() {

        TripForm form = new TripForm();
        form.setCity("Rome");
        form.setStartDate(LocalDate.of(2026, 10, 20));
        form.setEndDate(LocalDate.of(2026, 10, 10));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tripServiceImpl.createTrip(form, "test@mail.com")
        );

        assertEquals("Start date cannot be after end date", ex.getMessage());
        verify(tripRepository, never()).save(any());
        verify(userRepository, never()).findByEmail(any());
    }


    @Test
    void updateTrip_valid_update() {

        Trip existing = new Trip();
        existing.setId(1L);
        existing.setCity("Rome");
        existing.setStartDate(LocalDate.of(2026, 1, 1));
        existing.setEndDate(LocalDate.of(2026, 1, 10));

        TripForm form = new TripForm();
        form.setCity("Paris");
        form.setStartDate(LocalDate.of(2027, 1, 1));
        form.setEndDate(LocalDate.of(2027, 1, 10));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tripRepository.save(existing)).thenReturn(existing);

        Trip updated = tripServiceImpl.updateTrip(1L, form, "mail@mail.com");

        assertEquals("Paris", updated.getCity());
        verify(ownershipValidator, times(1)).assertUserOwnTrip(existing, "mail@mail.com");
        verify(tripRepository, times(1)).save(existing);
    }

    @Test
    void deleteTrip_success() {


        Trip tripFromRepo = new Trip();
        User owner = new User();
        owner.setEmail("mail@mail.com");
        tripFromRepo.setUser(owner);
        tripFromRepo.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripFromRepo));
        doNothing().when(ownershipValidator).assertUserOwnTrip(tripFromRepo, "mail@mail.com");

        tripServiceImpl.deleteTrip(1L, "mail@mail.com");

        verify(ownershipValidator, times(1)).assertUserOwnTrip(tripFromRepo, "mail@mail.com");
        verify(tripRepository, times(1)).delete(tripFromRepo);

    }

    @Test
    void updateTrip_invalidDates_throwsException() {
        TripForm form = new TripForm();
        form.setCity("Paris");
        form.setStartDate(LocalDate.of(2027, 1, 10));
        form.setEndDate(LocalDate.of(2027, 1, 1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tripServiceImpl.updateTrip(1L, form, "mail@mail.com")
        );

        assertEquals("Start date cannot be after end date", ex.getMessage());
        verify(tripRepository, never()).save(any());
    }

    @Test
    void getTrip_notFound_throwsResourceNotFoundException() {

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());


        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> tripServiceImpl.getTrip(1L, "mail@mail.com")
        );

        assertEquals("Trip not found", ex.getMessage());

    }

    @Test
    void getTrip_notOwned_throwsSecurityException() {

        Trip tripFromRepo = new Trip();
        User owner = new User();
        owner.setEmail("owner@mail.com");
        tripFromRepo.setUser(owner);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripFromRepo));

        doThrow(new AccessDeniedException("Forbidden"))
                .when(ownershipValidator)
                .assertUserOwnTrip(tripFromRepo, "mail@mail.com");

        assertThrows(
                AccessDeniedException.class,
                () -> tripServiceImpl.getTrip(1L, "mail@mail.com")
        );
    }

    @Test
    void getTripsForUser_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));

        Trip trip = new Trip();
        trip.setId(10L);
        trip.setCity("Rome");
        when(tripRepository.findByUserId(1L)).thenReturn(List.of(trip));

        List<TripResponse> trips = tripServiceImpl.getTripsForUser("user@mail.com");
        assertEquals(1, trips.size());
        assertEquals("Rome", trips.get(0).getCity());
    }

}
