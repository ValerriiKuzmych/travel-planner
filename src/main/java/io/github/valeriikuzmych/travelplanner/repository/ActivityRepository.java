package io.github.valeriikuzmych.travelplanner.repository;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTripId(Long tripId);
    
    List<Activity> findByTripIdOrderByDateAscStartTimeAsc(Long tripId);

}
