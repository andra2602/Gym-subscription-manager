package models;

import java.time.LocalTime;
import java.time.DayOfWeek;


public class TimeSlot {
    private LocalTime startTime;   // Ora de început a sesiunii
    private LocalTime endTime;     // Ora de final a sesiunii
    private DayOfWeek day;            // Ziua în care se desfășoară (ex: Luni, Marți)
    private Trainer trainer;       // Antrenorul disponibil în acel interval orar (opțional)

    public TimeSlot(LocalTime startTime, LocalTime endTime, DayOfWeek day, Trainer trainer) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.trainer = trainer;
    }

    /// getters
    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    /// setters
    public void setStartTime(LocalTime startTime) {
        if (startTime.isAfter(this.endTime)) {
            throw new IllegalArgumentException("Ora de început trebuie să fie înaintea orei de sfârșit.");
        }
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("Ora de sfârșit trebuie să fie după ora de început.");
        }
        this.endTime = endTime;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime + " " + day;
    }
}
