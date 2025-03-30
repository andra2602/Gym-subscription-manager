package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member extends User{
    private LocalDate registrationDate;
    private float weight;
    private float height;
    private String experienceLevel;
    private boolean hasTrainer;
    private Subscription subscription;
    private boolean isStudent;

    private List<Payment> payments = new ArrayList<>();

    public Member(String name, String username, String email, String phoneNumber, String cnp,
                  String password, LocalDate registrationDate, float weight, float height,
                  String experienceLevel, boolean hasTrainer, Subscription subscription,boolean isStudent) {
        super(name, username, email, phoneNumber, cnp, password);

        validateExperienceLevel(experienceLevel);
        // dacă registrationDate e null => punem data curentă
        this.registrationDate = (registrationDate != null) ? registrationDate : LocalDate.now();

        this.weight = weight;
        this.height = height;
        this.experienceLevel = experienceLevel.toLowerCase();
        this.hasTrainer = hasTrainer;
        this.subscription = subscription;
        this.isStudent = isStudent;
    }

    //validari
    private void validateExperienceLevel(String level) {
        if (level == null || !(level.equalsIgnoreCase("incepator") ||
                level.equalsIgnoreCase("intermediar") ||
                level.equalsIgnoreCase("avansat"))) {
            throw new IllegalArgumentException("Nivelul de experiență trebuie să fie: incepator, intermediar sau avansat.");
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
    public boolean getHasTrainer() {
        return hasTrainer;
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

    public void setHasTrainer(boolean hasTrainer) {
        this.hasTrainer = hasTrainer;
    }
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
    public void setStudent(boolean student) {
        this.isStudent = student;
    }
    public void addPayment(Payment payment) {
        payments.add(payment);
    }

}
