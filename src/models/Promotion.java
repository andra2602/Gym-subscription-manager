package models;
import java.time.LocalDate;
public class Promotion {
    private String name;
    private String description;
    private float discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;


    public Promotion(String name, String description, float discountPercent, LocalDate startDate, LocalDate endDate) {
        validateDescription(description);
        validateStartDate(startDate);
        validateEndDate(startDate, endDate);
        validateDiscount(discountPercent);

        this.name = name;
        this.description = description;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }
    ///  validari

    private void validateDescription(String description) {
        if (description == null || description.length() < 10 || description.length() > 200) {
            throw new IllegalArgumentException("Descrierea trebuie să aibă între 10 și 200 de caractere.");
        }
    }
    private void validateStartDate(LocalDate startDate) {
        if (startDate == null || startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de început trebuie să fie cel puțin data curentă.");
        }
    }
    private void validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Data de sfârșit trebuie să fie egală sau după data de început.");
        }
    }

    private void validateDiscount(float discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Reducerea trebuie să fie între 0% și 100%.");
        }
    }

    public boolean isValidNow() {
        LocalDate today = LocalDate.now();
        return active &&
                (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isBefore(endDate) || today.isEqual(endDate));
    }

    /// getters
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public float getDiscountPercent() {
        return discountPercent;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public boolean isActive() {
        return active;
    }



    /// setters
    public void setActive(boolean active) {
             this.active = active;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }
    public void setDiscountPercent(float discountPercent) {
        validateDiscount(discountPercent);
        this.discountPercent = discountPercent;
    }
    public void setStartDate(LocalDate startDate) {
        validateStartDate(startDate);
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        validateEndDate(this.startDate, endDate);
        this.endDate = endDate;
    }
}
