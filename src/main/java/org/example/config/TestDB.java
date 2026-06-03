package org.example.config;

import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Kết nối database THÀNH CÔNG!");
            } else {
                System.out.println("Kết nối database THẤT BẠI!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

