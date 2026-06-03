package org.example.service.impl;

import org.example.entity.WorkSchedule;
import org.example.repository.WorkScheduleRepository;
import org.example.repository.impl.WorkScheduleRepositoryImpl;
import org.example.service.WorkScheduleService;

import java.util.List;

public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository repo = new WorkScheduleRepositoryImpl();

    @Override
    public List<WorkSchedule> findAll() {
        return repo.findAll();
    }

    @Override
    public void update(int employeeId, int oldShiftId, int newShiftId, String workDate) {
        repo.update(employeeId, oldShiftId, newShiftId, workDate);
    }

    @Override
    public void delete(int empId, int shiftId, String workDate) {
        repo.delete(empId, shiftId, workDate);
    }
}
