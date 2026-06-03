package org.example.service.impl;

import org.example.repository.EmployeeShiftRepository;
import org.example.repository.impl.EmployeeShiftRepositoryImpl;
import org.example.service.EmployeeShiftService;

import java.time.LocalDate;

public class EmployeeShiftServiceImpl implements EmployeeShiftService {

    private final EmployeeShiftRepository repo =
            new EmployeeShiftRepositoryImpl();

    @Override
    public void assignShift(int empId, int shiftId, LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không được gán ca cho ngày trong quá khứ");
        }

        if (repo.exists(empId, shiftId, date)) {
            throw new IllegalArgumentException("Nhân viên đã được gán ca này trong ngày");
        }

        repo.assign(empId, shiftId, date);
    }
    @Override
    public void update(int empId, int oldShiftId, int newShiftId, String workDate) {
        repo.update(empId, oldShiftId, newShiftId, workDate);
    }

}
