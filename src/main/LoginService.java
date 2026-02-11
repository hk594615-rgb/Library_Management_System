import java.sql.*;

public class LoginService {
    
    // بيانات الاتصال بقاعدة البيانات (مثال)
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public boolean authenticate(String username, String password) {
        // استخدام علامة الاستفهام (?) يمنع الـ SQL Injection تماماً
        String query = "SELECT password FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                // مقارنة الـ Hash المدخل مع المخزن في القاعدة
                return storedHash.equals(PasswordUtils.hashPassword(password));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;
    }
}
