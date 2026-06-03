package org.example.session;

/**
 * UserSession - Lưu thông tin người dùng sau khi đăng nhập.
 * Dùng Singleton để truy cập từ bất kỳ đâu trong ứng dụng.
 */
public class UserSession {

    private static UserSession instance;

    private String username;
    private String role; // "ADMIN" hoặc "USER"

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // ===== GETTERS & SETTERS =====
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    // ===== Xóa session khi logout =====
    public void clear() {
        instance = null;
    }
}