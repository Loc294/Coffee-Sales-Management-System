package org.example.service;

import java.util.Map;

public interface StatisticService {
    int getCustomerCount();
    int getEmployeeCount();
    double getMonthlyRevenue();

    int getTotalInventory();

    int getExportedTotal();

    Map<Integer, Double> getRevenueByMonths();
}