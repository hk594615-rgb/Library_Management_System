import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    // إعداد قاعدة البيانات - سيتم إنشاء ملف باسم library.db تلقائياً
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        initializeDatabase(); // إنشاء الجداول عند التشغيل لأول مرة
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==========================================");
        System.out.println("   SECURE LIBRARY MANAGEMENT SYSTEM       ");
        System.out.println("==========================================");

        // 1. مرحلة تسجيل الدخول الآمن
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        if (authenticate(user, pass)) {
            System.out.println("\n[SUCCESS] Welcome, " + user);
            logAction(user, "Logged in.");

            boolean running = true;
            while (running) {
                System.out.println("\n--- Library Dashboard ---");
                System.out.println("1. Add New Book");
                System.out.println("2. Search Book by Title");
                System.out.println("3. Delete Book by ISBN");
                System.out.println("4. Exit");
                System.out.print("Select an option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // تنظيف السطر

                switch (choice) {
                    case 1:
                        System.out.print("Book Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Author: ");
                        String author = scanner.nextLine();
                        System.out.print("ISBN: ");
                        String isbn = scanner.nextLine();
                        if (addNewBook(title, author, isbn)) {
                            System.out.println("[INFO] Book added securely.");
                            logAction(user, "Added book: " + title);
                        }
                        break;
                    case 2:
                        System.out.print("Enter title to search: ");
                        searchBook(scanner.nextLine());
                        break;
                    case 3:
                        System.out.print("Enter ISBN to delete: ");
                        deleteBook(scanner.nextLine());
                        logAction(user, "Attempted to delete ISBN: " + isbn);
                        break;
                    case 4:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } else {
            System.out.println("\n[ALERT] Access Denied!");
            logAction(user, "Failed login attempt.");
        }
        System.out.println("Exiting...");
        scanner.close();
    }

    // --- وظائف النظام والأمان ---

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, isbn TEXT)");
            // مستخدم افتراضي: admin / كلمة المرور: admin123
            String adminPass = hashPassword("admin123");
            stmt.execute("INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '" + adminPass + "')");
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private static boolean authenticate(String user, String pass) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(hashPassword(pass));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    private static boolean addNewBook(String title, String author, String isbn) {
        String sql = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static void searchBook(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("-> " + rs.getString("title") + " | Author: " + rs.getString("author") + " | ISBN: " + rs.getString("isbn"));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private static void deleteBook(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            if (pstmt.executeUpdate() > 0) System.out.println("Done.");
            else System.out.println("Not found.");
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    private static void logAction(String user, String action) {
        try (FileWriter fw = new FileWriter("security_audit.log", true)) {
            fw.write("[" + LocalDateTime.now() + "] User: " + user + " - Action: " + action + "\n");
        } catch (IOException e) { }
    }
}
