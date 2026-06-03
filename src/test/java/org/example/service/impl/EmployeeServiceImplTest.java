package org.example.service.impl;

import org.example.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl();
    }

    /**
     * Hàm phụ trợ: Giúp lấy nhân viên mới nhất từ Database
     * để lấy được ID thật phục vụ cho Test Update và Delete
     */
    private Employee getLatestEmployee() {
        List<Employee> all = employeeService.findAll();
        if (all == null || all.isEmpty()) return null;
        return all.get(all.size() - 1);
    }

    @Test
    @DisplayName("1. Test lấy danh sách nhân viên")
    void findAll() {
        List<Employee> list = employeeService.findAll();
        assertNotNull(list);

        System.out.println("=== THỐNG KÊ NHÂN VIÊN ===");
        System.out.println("Tổng số nhân viên hiện có: " + list.size());
        if (!list.isEmpty()) {
            System.out.println("Nhân viên tiêu biểu: " + list.get(0).getName());
        }
    }

    @Test
    @DisplayName("2. Test thêm mới nhân viên")
    void create() {
        Employee emp = new Employee();
        String uniqueName = "NV " + System.currentTimeMillis() % 1000;
        emp.setName(uniqueName);
        emp.setPhone("0987654321");
        emp.setPosition("Staff");

        assertDoesNotThrow(() -> employeeService.create(emp));

        // Kiểm tra xem đã lưu vào DB chưa
        Employee latest = getLatestEmployee();
        assertNotNull(latest);
        assertTrue(latest.getName().contains(uniqueName));
    }

    @Test
    @DisplayName("3. Test cập nhật thông tin nhân viên")
    void update() {
        // Bước 1: Tạo 1 nhân viên mới để lấy ID thật
        create();
        Employee latest = getLatestEmployee();
        assertNotNull(latest, "Không có dữ liệu để update");

        // Bước 2: Thay đổi thông tin
        latest.setName(latest.getName() + " (Updated)");
        latest.setPosition("Manager");

        // Bước 3: Lưu cập nhật
        employeeService.update(latest);

        // Bước 4: Kiểm tra
        List<Employee> all = employeeService.findAll();
        boolean found = all.stream().anyMatch(e -> e.getName().contains("(Updated)"));
        assertTrue(found, "Tên nhân viên chưa được cập nhật trong DB");
    }

    @Test
    @DisplayName("4. Test xóa nhân viên")
    void deleteById() {
        // Bước 1: Tạo nhân viên mới để xóa
        create();
        Employee latest = getLatestEmployee();
        int targetId = latest.getId();

        // Bước 2: Thực hiện xóa
        employeeService.deleteById(targetId);

        // Bước 3: Kiểm tra xem ID đó còn tồn tại không
        List<Employee> all = employeeService.findAll();
        boolean stillExists = all.stream().anyMatch(e -> e.getId() == targetId);
        assertFalse(stillExists, "Nhân viên vẫn còn tồn tại sau khi xóa");
    }
}