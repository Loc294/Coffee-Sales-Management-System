package org.example.controller;

import org.example.service.StatisticService;
import org.example.service.impl.StatisticServiceImpl;

import java.util.Map;

public class StatisticController {
    private final StatisticService statisticService = new StatisticServiceImpl();

    public int getCustomerCount() {
        return statisticService.getCustomerCount();
    }

    public int getEmployeeCount() {
        return statisticService.getEmployeeCount();
    }

    public double getMonthlyRevenue() {
        return statisticService.getMonthlyRevenue();
    }

    public int getTotalInventory() {
        return statisticService.getTotalInventory();
    }

    public int getExportedTotal() {
        return statisticService.getExportedTotal();
    }

    public Map<Integer, Double> getRevenueByMonths() {
        return statisticService.getRevenueByMonths();
    }
}