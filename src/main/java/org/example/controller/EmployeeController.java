package org.example.controller;

import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.example.service.impl.EmployeeServiceImpl;

import java.util.List;

public class EmployeeController {

    private final EmployeeService service = new EmployeeServiceImpl();

    public List<Employee> getAll() {
        return service.findAll();
    }

    public void create(String name, String phone, String position, Integer accountId) {
        Employee emp = new Employee(name, phone, position, null);
        service.create(emp);
    }

    public void update(Employee emp) {
        service.update(emp);
    }

    public void delete(int id) {
        service.deleteById(id);
    }

    public Employee findById(int id) {
        for (Employee e : getAll()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

}
