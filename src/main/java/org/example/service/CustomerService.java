package org.example.service;

import org.example.entity.Customer;
import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    void create(Customer customer);

    void update(Customer customer);

    void deleteById(int id);

    Customer findById(int id);

    List<Customer> search(String keyword, String status);
}
