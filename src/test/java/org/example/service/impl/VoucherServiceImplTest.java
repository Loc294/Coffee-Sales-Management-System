package org.example.service.impl;

import org.example.entity.Voucher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VoucherServiceImplTest {

    private VoucherServiceImpl voucherService;

    @BeforeEach
    void setUp() {
        voucherService = new VoucherServiceImpl();
    }

    // --- TRƯỜNG HỢP 1: THÊM MỚI THÀNH CÔNG (Dữ liệu khớp DB) ---
    @Test
    @DisplayName("TC1: Thêm mới Voucher với đúng ENUM tiếng Việt")
    void testAddSuccess() {
        String code = "VC" + (System.currentTimeMillis() % 10000);
        Voucher v = new Voucher();
        v.setCode(code);
        v.setDiscountType("Số tiền"); // Khớp ENUM('Phần trăm','Số tiền')
        v.setDiscountValue(50000.0);
        v.setStartDate(LocalDate.now());
        v.setEndDate(LocalDate.now().plusDays(30));
        v.setStatus("Còn hiệu lực"); // Khớp ENUM('Còn hiệu lực','Hết hạn','Đã sử dụng')
        v.setUsageLimit(100);
        v.setUsedCount(0);
        v.setNote("Test thêm mới");

        assertDoesNotThrow(() -> voucherService.add(v));
    }

    // --- TRƯỜNG HỢP 2: TEST LOGIC TỰ ĐỘNG HẾT HẠN (Logic của Service) ---
    @Test
    @DisplayName("TC2: Voucher quá hạn phải tự động đổi trạng thái sang 'Hết hạn'")
    void testAutoUpdateStatus() {
        String code = "EX" + (System.currentTimeMillis() % 10000);
        Voucher v = new Voucher();
        v.setCode(code);
        v.setDiscountType("Phần trăm");
        v.setDiscountValue(15.0);
        v.setStartDate(LocalDate.now().minusDays(10));
        v.setEndDate(LocalDate.now().minusDays(1)); // Ngày kết thúc là hôm qua
        v.setStatus("Còn hiệu lực");
        v.setUsageLimit(10);
        v.setUsedCount(0);

        voucherService.add(v);

        // Kích hoạt logic quét ngày tháng trong hàm search
        voucherService.search(code, "Tất cả");

        // Kiểm tra kết quả sau khi quét
        Voucher updatedV = voucherService.findByCode(code);
        assertNotNull(updatedV);
        assertEquals("Hết hạn", updatedV.getStatus(), "Status phải tự động cập nhật về 'Hết hạn'");
    }

    // --- TRƯỜNG HỢP 3: TEST TÌM KIẾM THEO TỪ KHÓA ---
    @Test
    @DisplayName("TC3: Tìm kiếm Voucher theo mã code")
    void testSearchByKeyword() {
        List<Voucher> result = voucherService.search("TEST", "Tất cả");
        assertNotNull(result);
    }

    // --- TRƯỜNG HỢP 4: TEST TÌM KIẾM THEO TRẠNG THÁI ---
    @Test
    @DisplayName("TC4: Lọc Voucher theo trạng thái 'Hết hạn'")
    void testSearchByStatus() {
        List<Voucher> result = voucherService.search("", "Hết hạn");
        assertNotNull(result);
        for (Voucher v : result) {
            assertEquals("Hết hạn", v.getStatus());
        }
    }

    // --- TRƯỜNG HỢP 5: TEST XÓA VOUCHER ---
    @Test
    @DisplayName("TC5: Xóa Voucher theo ID")
    void testDelete() {
        // Test với ID không tồn tại để đảm bảo không lỗi SQL
        assertDoesNotThrow(() -> voucherService.delete(-1));
    }
}