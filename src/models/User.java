package models;

public class User {
    private static int nextId = 1;
    private int id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String cnp;
    private String password;


    public User() {
        this.id = nextId++;
    }
    public User(String name, String username, String email, String phoneNumber, String cnp, String password) {
        validateName(name);
        validateUsername(username);
        validateEmail(email);
        validatePhone(phoneNumber);
        validateCNP(cnp);
        validatePassword(password);

        this.id = nextId++;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cnp = cnp;
        this.password = password;
    }

    /// validari

    private void validateName(String name) {
        if (name == null || !name.matches("^[a-zA-Z\\s\\-]{10,}$"))
            throw new IllegalArgumentException("Numele trebuie să aibă minim 10 caractere și să conțină doar litere, spații sau liniuțe.");
    }

    private void validateUsername(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9]{5,20}$"))
            throw new IllegalArgumentException("Username-ul trebuie să fie alfanumeric, între 5 și 20 de caractere.");
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email-ul nu este valid. Ex: my.user@example.com");
    }

    private void validatePhone(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("^(\\+40|0)\\d{9}$"))
            throw new IllegalArgumentException("Numărul de telefon nu este valid. Ex: +40722123456 sau 0722123456");
    }

    private void validateCNP(String cnp) {
        if (cnp == null || !cnp.matches("^[1-8][0-9]{12}$") || !isValidCNP(cnp))
            throw new IllegalArgumentException("CNP-ul nu este valid (format greșit sau cifră de control incorectă).");
    }

    private void validatePassword(String password) {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^*&+\\-!]).{8,}$"))
            throw new IllegalArgumentException("Parola trebuie să aibă minim 8 caractere, o literă mare, o cifră și un caracter special.");
    }

    private boolean isValidCNP(String cnp) {
        int[] control = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};
        int sum = 0;

        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnp.charAt(i)) * control[i];
        }

        int checkDigit = sum % 11;
        if (checkDigit == 10) checkDigit = 1;

        return checkDigit == Character.getNumericValue(cnp.charAt(12));
    }

    /// getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getCnp() {
        return cnp;
    }


    /// setters cu validare
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setUsername(String username) {
        validateUsername(username);
        this.username = username;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        validatePhone(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public void setCnp(String cnp) {
        validateCNP(cnp);
        this.cnp = cnp;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", cnp='" + cnp + '\'' +
                '}';
    }


}
