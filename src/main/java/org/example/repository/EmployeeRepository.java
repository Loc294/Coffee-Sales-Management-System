package org.example.repository;

import org.example.entity.Employee;
import java.util.List;

public interface EmployeeRepository {

    List<Employee> findAll();

    Employee findById(int id);

    void save(Employee employee);

    void update(Employee employee);

    void deleteById(int id);
}
