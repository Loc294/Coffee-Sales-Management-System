package org.example.repository;

import java.time.LocalDate;

public interface EmployeeShiftRepository {

    void assign(int empId, int shiftId, LocalDate date);

    boolean exists(int empId, int shiftId, LocalDate date);
    void update(int empId, int oldShiftId, int newShiftId, String workDate);

}
