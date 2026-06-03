package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
    }

    @Test
    @DisplayName("TC1: Tạo đơn hàng thành công")
    void testCreateOrder() {
        List<OrderDetail> details = new ArrayList<>();
        details.add(new OrderDetail(1, "Black Coffee", 20000.0, 2));

        Order order = orderService.createOrder(details, "Test tạo đơn");

        assertNotNull(order);
        assertTrue(order.getId() > 0);

        // Kiểm tra thêm
        assertEquals("PENDING", order.getStatus());//trạng thái
        assertNotNull(order.getDetails());//dữ liệu
        assertFalse(order.getDetails().isEmpty());//số lượng
        // Nếu chạy đến đây mà không lỗi thì hiện thông báo
        System.out.println("TC1: PASS - Đã tạo đơn thành công vào Database.");
    }


    /**
     * HÀM TRỢ GIÚP: Tạo đơn hàng an toàn
     * Tự lấy ID sản phẩm có sẵn trong DB để tránh lỗi Foreign Key
     */
    private Order helperCreateOrder(String note) {
        // Lấy danh sách sản phẩm hiện có trong DB (Giả sử bạn có ProductService hoặc dùng trực tiếp Repo)
        // Nếu không, ta mặc định dùng ID 1 hoặc 2 vì bạn đã INSERT ở file SQL
        List<OrderDetail> details = new ArrayList<>();

        // Bạn đã INSERT 'Black Coffee' và 'Milk Coffee'.
        // Mình dùng ID 1 (Black Coffee) làm mặc định.
        details.add(new OrderDetail(1, "Black Coffee", 20000.0, 1));

        // Dùng Random để tránh trùng mã ORD-timestamp khi chạy test liên tục
        return orderService.createOrder(details, note + " #" + new Random().nextInt(1000));
    }
    /* ================= 2. TEST HỦY ĐƠN ================= */
    @Test
    @DisplayName("TC2: Hủy đơn hàng (PENDING -> CANCELLED)")
    void cancelOrder() {
        Order order = helperCreateOrder("Đơn để hủy");

        assertNotNull(order);
        assertEquals("PENDING", order.getStatus());

        orderService.cancelOrder(order.getId());

        Order updated = orderService.getOrderWithDetails(order.getId());

        assertNotNull(updated);
        assertEquals("CANCELLED", updated.getStatus());
    }



    // Hàm phụ để tạo đơn hàng mẫu nhanh chóng cho các bài test
    private Order createSampleOrder(String note) {
        List<OrderDetail> details = new ArrayList<>();
        // Sử dụng Constructor có sẵn trong file bạn gửi
        details.add(new OrderDetail(1, "Cà phê đen", 20000.0, 2));
        return orderService.createOrder(details, note);
    }

    /* ================= 3. TEST HOÀN THÀNH ================= */
    @Test
    @DisplayName("TC3: Hoàn thành đơn hàng (Chuyển PENDING -> COMPLETED)")
    void completeOrder() {
        Order order = helperCreateOrder("Đơn thanh toán");
        int id = order.getId();
        assertNotNull(order);
        assertEquals("PENDING", order.getStatus());
        orderService.completeOrder(id);

        Order updated = orderService.getOrderWithDetails(id);
        assertEquals("COMPLETED", updated.getStatus());
        System.out.println("TC3 - Đã hoàn thành đơn ID: " + id);
    }

    /* ================= 4. TEST XEM CHI TIẾT (GÁN ID THỦ CÔNG) ================= */
//    @Test
//    @DisplayName("TC4: Hiển thị 2 đơn hàng gán ID thủ công")
//    void getOrderWithDetails() {
//        // --- BẠN HÃY NHÌN VÀO BẢNG orders CỦA BẠN VÀ ĐIỀN 2 ID VÀO ĐÂY ---
//        // Ví dụ: Sau khi chạy TC1, DB sinh ra ID 17, 18 chẳng hạn
//        int id1 = 1;
//        int id2 = 2;
//
//        Order o1 = orderService.getOrderWithDetails(id1);
//        if (o1 != null) {
//            System.out.println(">>> CHI TIẾT ĐƠN " + id1 + ": " + o1.getOrderCode());
//            o1.getDetails().forEach(d -> System.out.println("  + " + d.getProductName() + " | SL: " + d.getQuantity()));
//        }
//
//        Order o2 = orderService.getOrderWithDetails(id2);
//        if (o2 != null) {
//            System.out.println(">>> CHI TIẾT ĐƠN " + id2 + ": " + o2.getOrderCode());
//            o2.getDetails().forEach(d -> System.out.println("  + " + d.getProductName() + " | SL: " + d.getQuantity()));
//        }
//
//        // Bài test này sẽ Pass nếu DB của bạn có ít nhất 1 trong 2 ID trên
//        assertTrue(o1 != null || o2 != null, "Lỗi: Cả 2 ID " + id1 + ", " + id2 + " đều không có trong DB");
//    }
    @Test
    @DisplayName("TC4: Lấy chi tiết đơn hàng")
    void testGetOrderWithDetails() {
        Order order = helperCreateOrder("Test detail");//tạo dữ liệu test

        Order result = orderService.getOrderWithDetails(order.getId());//gọi hàm cần test

        assertNotNull(result);//ktra kết quả: không được null
        assertEquals(order.getId(), result.getId());//lấy đúng đơn hàng
        assertNotNull(result.getDetails());//danh sách chi tiết
        assertFalse(result.getDetails().isEmpty());//danh sách khoong rỗng
    }

    /* ================= 5. TEST THỐNG KÊ ================= */
    @Test
    @DisplayName("TC5: Thống kê báo cáo đơn hàng")
    void getAllOrders() {
        List<Order> list = orderService.getAllOrders();
        assertNotNull(list);

        long pending = list.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long completed = list.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        assertTrue(list.size() >= pending + completed);

        System.out.println("\n===== BÁO CÁO COFFEE SHOP =====");
        System.out.println("Tổng số hóa đơn: " + list.size());
        System.out.println(" - Đang chờ: " + pending);
        System.out.println(" - Hoàn thành: " + completed);
        System.out.println("===============================\n");
    }
}
