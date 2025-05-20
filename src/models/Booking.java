package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private Trainer trainer;          // Antrenorul care susține sesiunea (dacă e antrenament personal)
    private Member member;            // Membrul care face rezervarea
    private LocalDate date;           // Data rezervării
    private LocalTime timeSlot;       // Ora rezervată (pentru o sesiune)
    private String purpose;           // Scopul rezervării (ex: "Yoga", "Personal Trainer")
    private FitnessClass fitnessClass; // Clasa de fitness, dacă este rezervată

    public Booking(Trainer trainer, Member member, LocalDate date, LocalTime timeSlot, String purpose) {
        this.trainer = trainer;
        this.member = member;
        this.date = date;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
        this.fitnessClass = null; // Nu este setată în cazul unui antrenament personal
    }

    public Booking(FitnessClass fitnessClass, Member member, LocalDate date, LocalTime timeSlot) {
        this.fitnessClass = fitnessClass;
        this.member = member;
        this.date = date;
        this.timeSlot = timeSlot;
        this.purpose = fitnessClass.getName(); // Scopul rezervării este numele clasei
        this.trainer = fitnessClass.getTrainer(); // Trainerul clasei de fitness
    }
    public Booking(){}

    /// getters
    public Trainer getTrainer() {
        return trainer;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    public String getPurpose() {
        return purpose;
    }

    public FitnessClass getFitnessClass() {
        return fitnessClass;
    }

    /// setters
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTimeSlot(LocalTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setFitnessClass(FitnessClass fitnessClass) {
        this.fitnessClass = fitnessClass;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "trainer=" + (trainer != null ? trainer.getName() : "N/A") +
                ", member=" + member.getName() +
                ", date=" + date +
                ", timeSlot=" + timeSlot +
                ", purpose='" + purpose + '\'' +
                ", fitnessClass=" + (fitnessClass != null ? fitnessClass.getName() : "N/A") +
                '}';
    }
}
