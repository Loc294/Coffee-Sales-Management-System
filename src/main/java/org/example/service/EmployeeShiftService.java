package org.example.service;

import java.time.LocalDate;

public interface EmployeeShiftService {
    void assignShift(int empId, int shiftId, LocalDate date);
    void update(int empId, int oldShiftId, int newShiftId, String workDate);
}
