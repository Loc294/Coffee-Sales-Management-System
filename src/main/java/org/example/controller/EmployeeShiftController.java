package org.example.controller;

import org.example.service.EmployeeShiftService;
import org.example.service.impl.EmployeeShiftServiceImpl;

import java.time.LocalDate;

public class EmployeeShiftController {

    private final EmployeeShiftService service =
            new EmployeeShiftServiceImpl();

    public void update(int empId, int oldShiftId, int newShiftId, String workDate) {
        service.update(empId, oldShiftId, newShiftId, workDate);
    }

    public void assignShift(int empId, int shiftId, LocalDate date) {
        service.assignShift(empId, shiftId, date);
    }
}
