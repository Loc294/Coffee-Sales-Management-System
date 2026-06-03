package org.example.repository;

import org.example.entity.Customer;
import java.util.List;

public interface CustomerRepository {

    void save(Customer c);

    void update(Customer c);

    void delete(int id);

    Customer findById(int id);

    List<Customer> findAll();

    List<Customer> search(String keyword, String status);
}
