package services;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;
    private static final String FILE_NAME = "audit.csv";

    private AuditService() {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {

            if (new java.io.File(FILE_NAME).length() == 0) {
                writer.write("action,timestamp\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to initialize audit log: " + e.getMessage());
        }
    }
    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }
    public void log(String action) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(action.trim() + "," + timestamp + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write audit log: " + e.getMessage());
        }
    }
}
