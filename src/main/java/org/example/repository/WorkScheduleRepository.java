package org.example.repository;

import org.example.entity.WorkSchedule;
import java.util.List;

public interface WorkScheduleRepository {

    List<WorkSchedule> findAll();

    void update(int employeeId, int oldShiftId, int newShiftId, String workDate);

    void delete(int employeeId, int shiftId, String workDate);

}
