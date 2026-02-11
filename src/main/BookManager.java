import java.sql.*;

public class BookManager {
    
    // مسار قاعدة البيانات (سيتم إنشاؤها تلقائياً عند أول تشغيل)
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public boolean addNewBook(String title, String author, String isbn) {
        // القاعدة الذهبية: لا تدمج النصوص مباشرة، استخدم الـ ? دائماً
        String sql = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // تنظيف المدخلات (Input Sanitization)
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }
}
