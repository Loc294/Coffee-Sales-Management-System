package org.example.service.impl;

import org.example.repository.StatisticRepository;
import org.example.repository.impl.StatisticRepositoryImpl;
import org.example.service.StatisticService;

import java.util.Map;

public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository reportRepository = new StatisticRepositoryImpl();

    @Override
    public int getCustomerCount() {
        return reportRepository.countCustomers();
    }

    @Override
    public int getEmployeeCount() {
        return reportRepository.countEmployees();
    }

    @Override
    public double getMonthlyRevenue() {
        return reportRepository.getMonthlyRevenue();
    }

    @Override
    public int getTotalInventory() {
        return reportRepository.getTotalInventory();
    }

    @Override
    public int getExportedTotal() {
        return reportRepository.getTotalExported();
    }

    @Override
    public Map<Integer, Double> getRevenueByMonths() {
        return reportRepository.getRevenueByMonths();
    }
}