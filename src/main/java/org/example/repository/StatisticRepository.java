package org.example.repository;

import java.util.Map;

public interface StatisticRepository {

    int countCustomers();
    int countEmployees();

    int getExportedTotal();

    double getMonthlyRevenue();
    int getTotalInventory();
    int getTotalExported();

    // Thêm phương thức lấy doanh thu theo từng tháng trong năm hiện tại
    Map<Integer, Double> getRevenueByMonths();
}