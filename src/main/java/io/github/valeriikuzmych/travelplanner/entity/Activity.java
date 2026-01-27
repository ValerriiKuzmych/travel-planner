package io.github.valeriikuzmych.travelplanner.entity;


import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_trip"))
    private Trip trip;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 120)
    private String note;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Activity activity)) return false;
        return Objects.equals(id, activity.id) && Objects.equals(trip, activity.trip) && Objects.equals(name, activity.name) && Objects.equals(note, activity.note) && Objects.equals(date, activity.date) && Objects.equals(startTime, activity.startTime) && Objects.equals(endTime, activity.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trip, name, note, date, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", trip=" + trip +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
