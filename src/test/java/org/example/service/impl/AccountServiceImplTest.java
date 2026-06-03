package org.example.service.impl;

import org.example.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
    }

    /* ================= TEST READ (TÌM KIẾM) ================= */

    @Test
    @DisplayName("Lấy tất cả danh sách - Phải trả về List không null")
    void testFindAll() {
        List<Account> list = accountService.findAll();
        assertNotNull(list);
        System.out.println("Kiểm tra findAll: OK");
    }

@Test
@DisplayName("Tìm theo Username hợp lệ")
void testFindByUsername_Valid() {
    Account acc = accountService.findByUsername("admin");

    assertNotNull(acc, "Account không được null");
    assertEquals("admin", acc.getUsername());
}

    /* ================= TEST CREATE (THÊM MỚI) ================= */
//Thêm nhưng để trống acoount -->  kỳ vọng lỗi
    @Test
    @DisplayName("Thêm mới thất bại - Account bị null")
    void testCreate_NullAccount() {
        assertThrows(IllegalArgumentException.class, () -> accountService.create(null));
    }

// ueser trống --->từ cối và ném lỗi
    @Test
    @DisplayName("Thêm mới thất bại - Username để trống")
    void testCreate_EmptyUsername() {
        Account acc = new Account("", "123", "USER", true);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> accountService.create(acc));
        assertEquals("Username không được để trống", ex.getMessage());
    }

    // trùng usser --->nesmm lỗi
    @Test
    @DisplayName("Thêm mới thất bại - Username đã tồn tại")
    void testCreate_DuplicateUsername() {
        Account acc = new Account("admin", "123", "USER", true);
        assertThrows(IllegalArgumentException.class, () -> accountService.create(acc));
    }

    //thêm thành công --> kì vọng create account
    @Test
    @DisplayName("Thêm mới thành công - Dữ liệu chuẩn")
    void testCreate_Success() {
        String uniqueUser = "user_" + System.currentTimeMillis();
        Account acc = new Account(uniqueUser, "pass123", "STAFF", true);

        assertDoesNotThrow(() -> accountService.create(acc));
    }

    /* ================= TEST UPDATE (CẬP NHẬT) ================= */

    @Test
    @DisplayName("Cập nhật thất bại - Username bị null")
    void testUpdate_NullUsername() {
        Account acc = new Account(null, "123", "USER", true);
        assertThrows(RuntimeException.class, () -> accountService.update(acc));
    }

    @Test
    @DisplayName("Cập nhật thất bại - Password trống")
    void testUpdate_EmptyPassword() {
        Account acc = new Account("admin", "  ", "USER", true);
        Exception ex = assertThrows(RuntimeException.class, () -> accountService.update(acc));
        assertEquals("Password không được trống", ex.getMessage());
    }

    /* ================= TEST DELETE & EXISTS ================= */

    @Test
    @DisplayName("Xóa tài khoản - Kiểm tra không gây crash app")
    void testDelete() {
        // Xóa một ID không tồn tại để xem hệ thống có chịu tải được không
        assertDoesNotThrow(() -> accountService.deleteById(-999));
    }

    @Test
    @DisplayName("Kiểm tra tồn tại - Username có thật")
    void testExists_True() {
        boolean exists = accountService.existsByUsername("admin");
        // Nếu DB có admin thì sẽ là true, không thì false.
        assertTrue(accountService.existsByUsername("admin"));
        System.out.println("Admin exists: " + exists);
    }
}