import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginService loginService = new LoginService();
        BookManager bookManager = new BookManager();

        System.out.println("=== Welcome to Secure Library System ===");
    
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        if (loginService.authenticate(user, pass)) {
            System.out.println("Login Successful!");
            LoggerService.logAction(user, "Logged in successfully.");

            System.out.println("\n--- Add a New Book ---");
            System.out.print("Book Title: ");
            String title = scanner.nextLine();
            System.out.print("Author: ");
            String author = scanner.nextLine();
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine();

            if (bookManager.addNewBook(title, author, isbn)) {
                System.out.println("Book added to database securely.");
                LoggerService.logAction(user, "Added book: " + title);
            } else {
                System.out.println("Failed to add book.");
            }

        } else {
            System.out.println("Invalid credentials! Security alert triggered.");
            LoggerService.logAction(user, "Failed login attempt.");
        }
        
        System.out.println("Exiting System...");
        scanner.close();
    }
}
