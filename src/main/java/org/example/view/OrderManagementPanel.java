package org.example.view;

import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderManagementPanel extends JPanel {

    // ===== STYLE =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_SLATE = new Color(0x64748B);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController();

    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    private static final NumberFormat VND =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadOrders();
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("🧾 Quản Lý Đơn Hàng");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ===== CENTER =====
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 20, 20, 20));
        panel.setBackground(BG);

        panel.add(buildActions(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);

        return panel;
    }

    // ===== ACTIONS =====
    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        // Bỏ nút "+ Tạo đơn"
        JButton btnView = createButton("📋 Hóa đơn", BTN_BLUE);
        JButton btnDone = createButton("✔ Hoàn thành", BTN_GREEN);
        JButton btnCancel = createButton("✖ Hủy", BTN_RED);
        JButton btnDelete = createButton("🗑 Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_SLATE);

        panel.add(btnView);
        panel.add(btnDone);
        panel.add(btnCancel);
        panel.add(btnDelete);
        panel.add(btnRefresh);

        btnRefresh.addActionListener(e -> loadOrders());
        btnView.addActionListener(e -> openInvoiceDialog());
        btnDone.addActionListener(e -> changeStatus("COMPLETED"));
        btnCancel.addActionListener(e -> changeStatus("CANCELLED"));
        btnDelete.addActionListener(e -> deleteOrder());

        return panel;
    }

    // ===== TABLE =====
    private JScrollPane buildTable() {
        String[] cols = {"ID", "Mã đơn", "Tổng tiền", "Trạng thái", "Ghi chú", "Ngày tạo"};
        orderTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(38);
        orderTable.setFont(FONT_BODY);
        orderTable.setSelectionBackground(ROW_SELECTED);
        orderTable.setShowVerticalLines(false);
        orderTable.setAutoCreateRowSorter(true);

        orderTable.removeColumn(orderTable.getColumnModel().getColumn(0)); // Ẩn ID

        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                if (col == 2 && value != null) { // Cột Trạng thái (đã ẩn ID nên STT 3 thành 2)
                    JLabel lb = new JLabel(value.toString(), CENTER);
                    lb.setOpaque(true);
                    lb.setFont(FONT_BOLD);
                    String v = value.toString();
                    if (v.equals("Hoàn thành")) { lb.setBackground(new Color(0xDCFCE7)); lb.setForeground(new Color(0x166534)); }
                    else if (v.equals("Chờ xử lý")) { lb.setBackground(new Color(0xFEF9C3)); lb.setForeground(new Color(0x854D0E)); }
                    else if (v.equals("Đã hủy")) { lb.setBackground(new Color(0xFEE2E2)); lb.setForeground(new Color(0x991B1B)); }
                    return lb;
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (isSelected) setBackground(ROW_SELECTED);
                else setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        return new JScrollPane(orderTable);
    }

    // ===== LOAD =====
    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> list = orderController.getAllOrders();
        for (Order o : list) {
            orderTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getOrderCode(),
                    VND.format(o.getTotalAmount()) + " ₫",
                    translateStatus(o.getStatus()),
                    o.getNote(),
                    o.getCreatedTime()
            });
        }
    }

    private void changeStatus(String status) {
        int row = orderTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!"); return; }
        int modelRow = orderTable.convertRowIndexToModel(row);
        int id = (int) orderTableModel.getValueAt(modelRow, 0);
        if ("COMPLETED".equals(status)) orderController.completeOrder(id);
        else orderController.cancelOrder(id);
        loadOrders();
    }

    // ===== LOGIC XÓA =====
    private void deleteOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn hàng này không?\nHành động này không thể hoàn tác.", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(row);
        int id = (int) orderTableModel.getValueAt(modelRow, 0);

        try {
            orderController.deleteOrder(id);
            JOptionPane.showMessageDialog(this, "Xóa đơn hàng thành công!");
            loadOrders();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String translateStatus(String s) {
        if ("PENDING".equals(s)) return "Chờ xử lý";
        if ("COMPLETED".equals(s)) return "Hoàn thành";
        if ("CANCELLED".equals(s)) return "Đã hủy";
        return s;
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    // Đã xóa phương thức openCreateOrderDialog()

    // ===== XEM HÓA ĐƠN =====
    private void openInvoiceDialog() {
        int row = orderTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng!"); return; }
        int modelRow = orderTable.convertRowIndexToModel(row);
        int id = (int) orderTableModel.getValueAt(modelRow, 0);
        Order order = orderController.getOrderById(id);
        List<OrderDetail> details = orderController.getOrderDetails(id);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        
        StringBuilder sb = new StringBuilder();
        sb.append("      COFFEE PROJECT - HÓA ĐƠN\n");
        sb.append("--------------------------------------\n");
        sb.append("Mã đơn: ").append(order.getOrderCode()).append("\n");
        sb.append("Ngày:   ").append(order.getCreatedTime()).append("\n");
        sb.append("Trạng thái: ").append(translateStatus(order.getStatus())).append("\n");
        sb.append("--------------------------------------\n");
        sb.append(String.format("%-18s %3s %10s\n", "Tên món", "SL", "T.Tiền"));
        
        for (OrderDetail d : details) {
            sb.append(String.format("%-18s %3d %10s\n", 
                d.getProductName(), d.getQuantity(), VND.format(d.getUnitPrice() * d.getQuantity())));
        }
        
        sb.append("--------------------------------------\n");
        sb.append("TỔNG CỘNG:            ").append(VND.format(order.getTotalAmount())).append(" ₫\n");
        sb.append("--------------------------------------\n");
        sb.append("Ghi chú: ").append(order.getNote() != null ? order.getNote() : "").append("\n\n");
        sb.append("   CẢM ƠN QUÝ KHÁCH. HẸN GẶP LẠI!");

        area.setText(sb.toString());
        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        dialog.add(btnClose, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}