package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FitnessClass {
    private String name;
    private int duration; // în minute
    private String difficulty;
    private Trainer trainer;
    private List<Member> participants;
    private LocalDate date;
    private LocalTime hour;
    private int maxParticipants;


    public FitnessClass(String name, int duration, String difficulty,
                        Trainer trainer, List<Member> participants,
                        LocalDate date, LocalTime hour, int maxParticipants) {
        validateDifficulty(difficulty);
        this.name = name;
        this.duration = duration;
        this.difficulty = difficulty;
        this.trainer = trainer;
        this.participants = participants;
        this.date = date;
        this.hour = hour;
        this.maxParticipants = maxParticipants;
    }

    /// validari
    private void validateDifficulty(String difficulty) {
        if (difficulty == null ||
                !(difficulty.equalsIgnoreCase("incepator") ||
                        difficulty.equalsIgnoreCase("intermediar") ||
                        difficulty.equalsIgnoreCase("avansat"))) {
            throw new IllegalArgumentException("Nivelul de dificultate trebuie să fie: incepator, intermediar sau avansat.");
        }
    }

    public boolean addParticipant(Member member) {
        if (participants.size() >= maxParticipants) {
            System.out.println("Clasa este plină. Nu se mai pot adăuga/inscrie participanți.");
            return false;
        }
        participants.add(member);
        return true;
    }


    /// getters
    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public List<Member> getParticipants() {
        return participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getHour() {
        return hour;
    }

    /// setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }
}
