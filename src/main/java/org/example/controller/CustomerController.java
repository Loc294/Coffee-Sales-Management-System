package org.example.controller;

import org.example.entity.Customer;
import org.example.service.CustomerService;
import org.example.service.impl.CustomerServiceImpl;

import java.util.List;

public class CustomerController {

    private final CustomerService service = new CustomerServiceImpl();

    /* ================= SEARCH ================= */
    public List<Customer> search(String keyword, String status) {
        return service.search(keyword, status);
    }

    /* ================= ADD ================= */
    public void add(String code, String name, String phone, String email, boolean active) {

        if (code == null || code.isBlank()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Tên khách hàng không được để trống");
        }
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }

        Customer c = new Customer();
        c.setCode(code);
        c.setName(name);
        c.setPhone(phone);
        c.setEmail(email);
        c.setPoint(0);
        c.setStatus(active ? 1 : 0);

        service.create(c);
    }

    /* ================= FIND BY ID ================= */
    public Customer findById(int id) {
        return service.findById(id);
    }

    /* ================= UPDATE ================= */
    public void update(int id, String name, String phone, String email, boolean active) {

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Tên khách hàng không được để trống");
        }
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }

        Customer c = service.findById(id);
        if (c == null) {
            throw new RuntimeException("Không tìm thấy khách hàng");
        }

        c.setName(name);
        c.setPhone(phone);
        c.setEmail(email);
        c.setStatus(active ? 1 : 0);

        service.update(c);
    }

    /* ================= DELETE ================= */
    public void delete(int id) {
        service.deleteById(id);
    }
}