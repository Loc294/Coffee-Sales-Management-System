package org.example.repository;

public interface AccountEmployeeRepository {
    void createAccountAndEmployee(
            String username,
            String password,
            String role,
            String name,
            String phone,
            String position
    );

}
