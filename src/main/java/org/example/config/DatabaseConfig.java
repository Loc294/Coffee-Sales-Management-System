package org.example.config;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffee" +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy driver SQL Server", e);
        }
    }

    public static Connection getConnection() {
        try {
            return java.sql.DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Không thể kết nối đến cơ sở dữ liệu", e);
        }
    }
}