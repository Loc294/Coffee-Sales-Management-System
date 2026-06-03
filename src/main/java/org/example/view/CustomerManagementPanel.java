package org.example.view;

import org.example.controller.CustomerController;
import org.example.entity.Customer;
import org.example.util.ExportToExcel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class CustomerManagementPanel extends JPanel {

    // ===== COLORS =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_AMBER = new Color(0xF59E0B);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_SLATE = new Color(0x64748B);

    private static final Color BADGE_ACTIVE = new Color(0xDCFCE7);
    private static final Color BADGE_STOP = new Color(0xFEE2E2);

    // ===== FONTS =====
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final CustomerController controller = new CustomerController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCountLabel;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        initUI();
        loadData();
    }

    /* ================= UI INITIALIZATION ================= */
    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    /* ================= HEADER ================= */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Quản Lý Khách Hàng");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Thêm, sửa, xóa và quản lý thông tin khách hàng");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(titleLabel);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        rowCountLabel = new JLabel("0 khách hàng");
        rowCountLabel.setFont(FONT_BOLD);
        rowCountLabel.setForeground(new Color(0xCBD5E1));
        header.add(rowCountLabel, BorderLayout.EAST);

        return header;
    }

    /* ================= CENTER ================= */
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(16, 20, 20, 20));
        p.setBackground(BG);

        p.add(buildControl(), BorderLayout.NORTH);
        p.add(buildTable(), BorderLayout.CENTER);

        return p;
    }

    /* ================= CONTROL BAR ================= */
    private JPanel buildControl() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên hoặc SĐT...");

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngưng"});
        cbStatus.setPreferredSize(new Dimension(130, 36));
        cbStatus.setFont(FONT_BODY);

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);

        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnSearch);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_SLATE);
        JButton btnExport = createButton("↓ Excel", BTN_BLUE);

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        right.add(btnExport);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // events
        btnSearch.addActionListener(e -> loadData());
        cbStatus.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadData();
        });
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnExport.addActionListener(e -> ExportToExcel.export(table, "DanhSachKhachHang.xlsx"));

        return bar;
    }

    /* ================= TABLE ================= */
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Mã", "Tên", "SĐT", "Email", "Điểm", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(ROW_SELECTED);
        table.getTableHeader().setFont(FONT_BOLD);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionForeground(new Color(0x1E40AF));
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.removeColumn(table.getColumnModel().getColumn(0)); // Hide ID column

        // Custom cell renderer (zebra + status badge)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                // Col 6 = "Trạng thái" (after ID hidden) — render as badge
                if (col == 6 && value != null) {
                    boolean active = value.toString().equals("Hoạt động");
                    JLabel badge = new JLabel(active ? "● Hoạt động" : "● Ngưng");
                    badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(CENTER);

                    if (isSelected) {
                        badge.setBackground(ROW_SELECTED);
                        badge.setForeground(active ? BTN_GREEN.darker() : BTN_RED.darker());
                    } else {
                        badge.setBackground(active ? BADGE_ACTIVE : BADGE_STOP);
                        badge.setForeground(active ? BTN_GREEN.darker() : BTN_RED.darker());
                    }
                    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
                    return badge;
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    setBackground(ROW_SELECTED);
                    setForeground(new Color(0x1E40AF));
                } else {
                    setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    setForeground(new Color(0x1E293B));
                }

                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        List<Customer> list = controller.search(
                txtSearch.getText(),
                Objects.requireNonNull(cbStatus.getSelectedItem()).toString()
        );

        int i = 1;
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    i++,
                    c.getCode(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getPoint(),
                    c.getStatus() == 1 ? "Hoạt động" : "Ngưng"
            });
        }

        rowCountLabel.setText(list.size() + " khách hàng");
    }

    /* ================= CRUD OPERATIONS ================= */
    private void openAddDialog() {
        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JCheckBox chkActive = new JCheckBox("Hoạt động", true);

        JPanel formPanel = createFormPanel();
        addField(formPanel, "Mã khách hàng:", txtCode);
        addField(formPanel, "Tên khách hàng:", txtName);
        addField(formPanel, "Số điện thoại:", txtPhone);
        addField(formPanel, "Email:", txtEmail);
        addField(formPanel, "Trạng thái:", chkActive);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "➕ Thêm khách hàng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.add(
                        txtCode.getText().trim(),
                        txtName.getText().trim(),
                        txtPhone.getText().trim(),
                        txtEmail.getText().trim(),
                        chkActive.isSelected()
                );
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Customer customer = controller.findById(id);

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtName = new JTextField(customer.getName());
        JTextField txtPhone = new JTextField(customer.getPhone());
        JTextField txtEmail = new JTextField(customer.getEmail());
        JCheckBox chkActive = new JCheckBox("Hoạt động", customer.getStatus() == 1);

        JPanel formPanel = createFormPanel();
        // Mã khách hàng không cho sửa
        addField(formPanel, "Mã khách hàng:", new JLabel(customer.getCode()));
        addField(formPanel, "Tên khách hàng:", txtName);
        addField(formPanel, "Số điện thoại:", txtPhone);
        addField(formPanel, "Email:", txtEmail);
        addField(formPanel, "Trạng thái:", chkActive);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "✎ Sửa khách hàng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.update(
                        id,
                        txtName.getText().trim(),
                        txtPhone.getText().trim(),
                        txtEmail.getText().trim(),
                        chkActive.isSelected()
                );
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String customerName = (String) tableModel.getValueAt(row, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khách hàng \"" + customerName + "\" không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.delete(id);
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================= UI HELPER METHODS ================= */
    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 12));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(Color.WHITE);
        return p;
    }

    private void addField(JPanel p, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(new Color(0x374151));
        p.add(lbl);
        comp.setFont(FONT_BODY);
        p.add(comp);
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }
}
