package validation;

public class Validator {

    // Validare pentru nume (minim 10 caractere, litere, spații sau liniuțe)
    public static void validateName(String name) {
        if (name == null || !name.matches("^[a-zA-Z\\s\\-]{10,}$"))
            throw new IllegalArgumentException("Numele trebuie să aibă minim 10 caractere și să conțină doar litere, spații sau liniuțe.");
    }

    // Validare pentru username (alfanumeric, între 5 și 20 de caractere)
    public static void validateUsername(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9]{5,20}$"))
            throw new IllegalArgumentException("Username-ul trebuie să fie alfanumeric, între 5 și 20 de caractere.");
    }

    // Validare pentru email
    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email-ul nu este valid. Ex: my.user@example.com");
    }

    // Validare pentru numărul de telefon (format corect)
    public static void validatePhone(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("^(\\+40|0)\\d{9}$"))
            throw new IllegalArgumentException("Numărul de telefon nu este valid. Ex: +40722123456 sau 0722123456");
    }

    // Validare pentru parolă (minim 8 caractere, literă mare, cifră și caracter special)
    public static void validatePassword(String password) {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^*&+\\-!]).{8,}$"))
            throw new IllegalArgumentException("Parola trebuie să aibă minim 8 caractere, o literă mare, o cifră și un caracter special.");
    }

    // Validare experiență nivel
    public static void validateExperienceLevel(String level) {
        if (level == null || !(level.equalsIgnoreCase("beginner") ||
                level.equalsIgnoreCase("intermediate") ||
                level.equalsIgnoreCase("advanced"))) {
            throw new IllegalArgumentException("Experience level must be: beginner, intermediate or advanced.");
        }
    }

    // Validare greutate
    public static void validateWeight(float weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Greutatea trebuie să fie un număr pozitiv.");
        }
    }

    // Validare înălțime
    public static void validateHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Înălțimea trebuie să fie un număr pozitiv.");
        }
    }

    public static void validateYearsOfExperience(double yearsOfExperience) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Anii de experiență trebuie să fie un număr pozitiv.");
        }
    }

    public static void validatePricePerHour(double pricePerHour) {
        if (pricePerHour <= 0) {
            throw new IllegalArgumentException("Prețul pe oră trebuie să fie un număr pozitiv.");
        }
    }

}
