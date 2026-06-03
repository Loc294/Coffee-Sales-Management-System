package org.example.service.impl;

import org.example.entity.Employee;
import org.example.event.DataChangeEventBus;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl() {
        this.employeeRepository = new EmployeeRepositoryImpl();
    }

    /* ================= FIND ALL ================= */
    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    /* ================= CREATE ================= */
    @Override
    public void create(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee không được null");
        }

        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }


        employeeRepository.save(employee);

        DataChangeEventBus.notifyChange();
    }

    /* ================= UPDATE ================= */
    @Override
    public void update(Employee employee) {

        if (employee == null || employee.getId() <= 0) {
            throw new IllegalArgumentException("Employee không hợp lệ");
        }

        employeeRepository.update(employee);

        DataChangeEventBus.notifyChange();
    }

    /* ================= DELETE ================= */
    @Override
    public void deleteById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID nhân viên không hợp lệ");
        }

        employeeRepository.deleteById(id);

        DataChangeEventBus.notifyChange();
    }
}
