import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoggerService {
    private static final String LOG_FILE = "security_audit.log";

    public static void logAction(String user, String action) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String logEntry = String.format("[%s] User: %s - Action: %s\n", 
                                            LocalDateTime.now(), user, action);
            fw.write(logEntry);
        } catch (IOException e) {
            System.err.println("Could not write to log file: " + e.getMessage());
        }
    }
}
