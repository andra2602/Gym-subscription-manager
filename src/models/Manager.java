package models;

public final class Manager {
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;

    public Manager(String name, String username, String email, String phoneNumber, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    /// getters
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }

    ///  setters
    public void setPassword(String password) { this.password = password;}
    public void setUsername(String username) { this.username = username; }


    // Funcționalități specifice Managerului
    public void manageMembers() {
        // Logica pentru gestionarea membrilor
        System.out.println("Managerul gestionează membrii.");
    }

    public void manageTrainers() {
        // Logica pentru gestionarea membrilor
        System.out.println("Managerul gestionează antrenorii.");
    }


    public void manageRevenue() {
        // Logica pentru gestionarea veniturilor
        System.out.println("Managerul gestionează veniturile.");
    }

    public void auditActions() {
        // Logica pentru auditarea acțiunilor
        System.out.println("Managerul auditează acțiunile.");
    }

    public void createPromotion() {
        // Logica pentru crearea promoțiilor
        System.out.println("Managerul creează promoții.");
    }
}
