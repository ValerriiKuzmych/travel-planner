package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
        verify(ownershipValidator, times(1)).assertUserOwnTrip(1L, "mail@mail.com");
        verify(tripRepository, times(1)).save(existing);
    }

    @Test
    void deleteTrip_success() {

        Long id = 1L;

        doNothing().when(ownershipValidator).assertUserOwnTrip(id, "mail@mail.com");

        tripServiceImpl.deleteTrip(id, "mail@mail.com");

        verify(ownershipValidator, times(1)).assertUserOwnTrip(id, "mail@mail.com");
        verify(tripRepository, times(1)).deleteById(id);
    }

}
