package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Trainer extends User {
    private String specialization;
    private double yearsOfExperience;
    private double pricePerHour;

    private Set<Member> trainedMembers;
    private List<TimeSlot> availableSlots;
    private List<Booking> bookings;
    private List<Integer> reviewScores;
    private Map<String, FitnessClass> coordinatedClasses;


    public Trainer(String name, String username, String email, String phoneNumber,String password,
                   String specialization, double yearsOfExperience, double pricePerHour,
                   Set<Member> trainedMembers, List<TimeSlot> availableSlots, List<Booking> bookings,
                   List<Integer> reviewScores) {
        super(name, username, email, phoneNumber, password);
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.pricePerHour = pricePerHour;
        this.trainedMembers = trainedMembers;
        this.availableSlots = availableSlots;
        this.bookings = bookings;
        this.reviewScores = reviewScores;
        this.coordinatedClasses = new HashMap<>();
    }

    public Trainer(){

    }

    /// getters
    public String getSpecialization() {
        return specialization;
    }
    public double getYearsOfExperience() {
        return yearsOfExperience;
    }
    public double getPricePerHour() {
        return pricePerHour;
    }
    public Set<Member> getTrainedMembers() {
        return trainedMembers;
    }
    public List<TimeSlot> getAvailableSlots() {
        return availableSlots;
    }
    public List<Booking> getBookings() {
        return bookings;
    }
    public List<Integer> getReviewScores() {
        return reviewScores;
    }

    // Review-ul calculat automat
    public double getReview() {
        if (reviewScores == null || reviewScores.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (int score : reviewScores) {
            sum += score;
        }

        double average = sum / reviewScores.size();
        return Math.min(average, 5.0);
    }


    public Map<String, FitnessClass> getCoordinatedClasses() {
        return coordinatedClasses;
    }



    /// setters
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public void setYearsOfExperience(double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    public void setTrainedMembers(Set<Member> trainedMembers) {
        this.trainedMembers = trainedMembers;
    }
    public void setAvailableSlots(List<TimeSlot> availableSlots) {
        this.availableSlots = availableSlots;
    }
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    public void setReviewScores(List<Integer> reviewScores) {
        this.reviewScores = reviewScores;
    }
    public void setCoordinatedClasses(Map<String, FitnessClass> coordinatedClasses) {
        this.coordinatedClasses = coordinatedClasses;
    }

    ///  functionalitate pentru lasarea de review-uri
    public void addReview(int score) {
        if (score < 0 || score > 5) {
            throw new IllegalArgumentException("Scorul trebuie să fie între 0 și 5.");
        }
        this.reviewScores.add(score);
    }

    @Override
    public String toString() {
        return "Trainer Name: " + getName() + "\n" +
                "Specialization: " + specialization + "\n" +
                "Years of Experience: " + yearsOfExperience + "\n" +
                "Price per Hour: " + pricePerHour;
    }
}
