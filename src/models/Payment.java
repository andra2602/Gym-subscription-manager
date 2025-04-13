package models;
import java.time.LocalDate;

public class Payment {
    private float amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod; /// Enum: CARD, CASH, ONLINE
    private Member member;
    private String purpose; /// ex: "Abonament lunar", "Clase yoga"

    public Payment(float amount, LocalDate paymentDate, PaymentMethod paymentMethod, Member member, String purpose) {
        this.amount = amount;
        this.paymentDate = (paymentDate != null) ? paymentDate : LocalDate.now();
        this.paymentMethod = paymentMethod;
        this.member = member;
        this.purpose = purpose;
    }


    /// getters
    public float getAmount() {
        return amount;
    }
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public Member getMember() {
        return member;
    }
    public String getPurpose() {
        return purpose;
    }

    // setters
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    //toString (pentru afișare în istoric)
    @Override
    public String toString() {
        return "Payment :" +
                "Amount = " + amount + " RON, " +
                "Date = " + paymentDate +
                ", Method = " + paymentMethod +
                ", Purpose = '" + purpose + '\'';
    }
}
