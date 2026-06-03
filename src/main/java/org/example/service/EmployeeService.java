package org.example.service;

import org.example.entity.Employee;
import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();


    void create(Employee employee);

    void update(Employee employee);

    void deleteById(int id);

}
