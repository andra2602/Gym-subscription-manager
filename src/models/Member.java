package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member extends User{
    private LocalDate registrationDate;
    private float weight;
    private float height;
    private String experienceLevel;
    private Trainer trainer;
    private Subscription subscription;
    private boolean isStudent;

    private List<Payment> payments = new ArrayList<>();

    public Member(String name, String username, String email, String phoneNumber,
                  String password, LocalDate registrationDate, float weight, float height,
                  String experienceLevel, Trainer trainer, Subscription subscription,boolean isStudent) {
        super(name, username, email, phoneNumber,password);

        validateExperienceLevel(experienceLevel);
        // dacă registrationDate e null => punem data curentă
        this.registrationDate = (registrationDate != null) ? registrationDate : LocalDate.now();
        this.weight = weight;
        this.height = height;
        this.experienceLevel = experienceLevel.toLowerCase();
        this.trainer = trainer;
        this.subscription = null;
        this.isStudent = isStudent;
    }

    //validari beginner, intermediate, advanced
    private void validateExperienceLevel(String level) {
        if (level == null || !(level.equalsIgnoreCase("beginner") ||
                level.equalsIgnoreCase("intermediate") ||
                level.equalsIgnoreCase("advanced"))) {
            throw new IllegalArgumentException("Experience level must be: beginner, intermediate or advanced.");
        }
    }

    //getters
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    public float getWeight() {
        return weight;
    }
    public float getHeight() {
        return height;
    }
    public String getExperienceLevel() {
        return experienceLevel;
    }
    public Trainer getTrainer() {
        return trainer;
    }
    public Subscription getSubscription() {
        return subscription;
    }
    public boolean isStudent() {
        return isStudent;
    }
    public List<Payment> getPayments() {
        return payments;
    }


    //setters
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public void setExperienceLevel(String experienceLevel) {
        validateExperienceLevel(experienceLevel);
        this.experienceLevel = experienceLevel.toLowerCase();
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
    public void setStudent(boolean student) {
        this.isStudent = student;
    }
    public void setPayments(List<Payment> payments) {this.payments = payments;}
    public void addPayment(Payment payment) {
        payments.add(payment);
    }



    @Override
    public String toString() {
        return "Member Name: " + getName() + "\n" +
                "Registration Date: " + registrationDate + "\n" +
                "Level of Experience: " + experienceLevel + "\n" +
                "Student : " + isStudent + "\n" +
                "Has trainer: " + (trainer != null ? trainer.getName() : "No trainer assigned") + "\n";
    }

}
