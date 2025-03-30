package models;

import java.time.LocalTime;

public class TimeSlot {
    private LocalTime startTime;   // Ora de început a sesiunii
    private LocalTime endTime;     // Ora de final a sesiunii
    private String day;            // Ziua în care se desfășoară (ex: Luni, Marți)
    private Trainer trainer;       // Antrenorul disponibil în acel interval orar (opțional)

    public TimeSlot(LocalTime startTime, LocalTime endTime, String day, Trainer trainer) {
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

    public String getDay() {
        return day;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    /// setters
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", day='" + day + '\'' +
                ", trainer=" + (trainer != null ? trainer.getName() : "N/A") +
                '}';
    }
}
