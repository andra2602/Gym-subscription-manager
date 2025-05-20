package models;

import java.time.LocalDate;

public class Subscription {
    public String type; /// lunar - 6 luni - anual
    public LocalDate startDate;
    public float price;
    public boolean isActive;
    private Promotion promotion;
    private int extendedMonths = 0;
    private int id;

    private static final float STUDENT_DISCOUNT = 0.30f;

    public Subscription(String type, LocalDate startDate, float price, boolean isActive, Promotion promotion) {
        this.type = type;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.price = price;
        this.isActive = isActive;
        this.promotion = promotion;
    }
    public Subscription(int id, String type, LocalDate startDate, float price,
                        boolean isActive, Promotion promotion, int extendedMonths) {
        this(type, startDate, price, isActive, promotion); // apelează constructorul tău actual
        this.extendedMonths = extendedMonths;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    /// metode de gestionare
    public LocalDate getEndDate() {
        int baseMonths = switch (type.toLowerCase()) {
            case "monthly" -> 1;
            case "6 months" -> 6;
            case "annual" -> 12;
            default -> 0;
        };

        return startDate.plusMonths(baseMonths + extendedMonths);
    }

    public boolean isCurrentlyActive() {
        return isActive && LocalDate.now().isBefore(getEndDate());
    }

    public float getFinalPriceForMember(Member member) {
        float finalPrice = price;

        if (promotion != null && promotion.isValidNow()) {
            finalPrice -= price * (promotion.getDiscountPercent() / 100);
        }

        if (member.isStudent()) {
            finalPrice -= price * STUDENT_DISCOUNT;
        }

        return finalPrice;
    }
    public void addMonths(int months) {
        this.extendedMonths += months;
    }

    public int getExtendedMonths() {
        return extendedMonths;
    }

    ///  getters & setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

}

