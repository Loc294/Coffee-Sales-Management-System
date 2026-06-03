package org.example.view;

import org.example.controller.VoucherController;
import org.example.entity.Voucher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VoucherManagementPanel extends JPanel {

    // ── Palette (Đồng bộ với các Panel khác) ───────────────────────────────
    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);

    private static final Color BTN_GREEN  = new Color(0x22C55E);
    private static final Color BTN_AMBER  = new Color(0xF59E0B);
    private static final Color BTN_RED    = new Color(0xEF4444);
    private static final Color BTN_SLATE  = new Color(0x64748B);
    private static final Color BTN_BLUE   = new Color(0x3B82F6);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    private final VoucherController controller = new VoucherController();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private JLabel rowCountLabel;

    public VoucherManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        initUI();
        loadData();
    }

    private void initUI() {
        /* ===== HEADER (BANNER) ===== */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🎟");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Quản Lý Voucher");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Tạo mã giảm giá, khuyến mãi và quản lý thời hạn sử dụng");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(titleLabel);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        rowCountLabel = new JLabel("0 voucher");
        rowCountLabel.setFont(FONT_BOLD);
        rowCountLabel.setForeground(new Color(0xCBD5E1));
        header.add(rowCountLabel, BorderLayout.EAST);

        /* ===== CONTROL BAR ===== */
        JPanel controlBar = new JPanel(new BorderLayout(12, 0));
        controlBar.setOpaque(false);
        controlBar.setBorder(new EmptyBorder(16, 20, 12, 20));

        // Search Group
        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm mã voucher...");

        cbStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Còn hiệu lực", "Hết hạn", "Đã sử dụng"});
        cbStatusFilter.setPreferredSize(new Dimension(140, 36));
        cbStatusFilter.setFont(FONT_BODY);

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);
        searchGroup.add(txtSearch);
        searchGroup.add(cbStatusFilter);
        searchGroup.add(btnSearch);

        // Action Group
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        JButton btnAdd     = createButton("＋ Thêm",    BTN_GREEN);
        JButton btnEdit    = createButton("✎ Sửa",     BTN_AMBER);
        JButton btnDelete  = createButton("✕ Xóa",     BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_SLATE);

        actionGroup.add(btnAdd);
        actionGroup.add(btnEdit);
        actionGroup.add(btnDelete);
        actionGroup.add(btnRefresh);

        controlBar.add(searchGroup, BorderLayout.WEST);
        controlBar.add(actionGroup, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(BG);
        northContainer.add(header, BorderLayout.NORTH);
        northContainer.add(controlBar, BorderLayout.CENTER);
        add(northContainer, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(new String[]{
                "ID", "Mã", "Loại", "Giá trị", "Bắt đầu", "Kết thúc", "Trạng thái", "Giới hạn", "Đã dùng", "Ghi chú"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(ROW_SELECTED);
        table.getTableHeader().setFont(FONT_BOLD);
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sp.getViewport().setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        centerPanel.add(sp, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cbStatusFilter.setSelectedIndex(0); loadData(); });
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<Voucher> list = controller.getAll(txtSearch.getText(), cbStatusFilter.getSelectedItem().toString());
            for (Voucher v : list) {
                model.addRow(new Object[]{
                        v.getId(), v.getCode(), v.getDiscountType(), v.getDiscountValue(),
                        v.getStartDate().format(fmt), v.getEndDate().format(fmt),
                        v.getStatus(), v.getUsageLimit(), v.getUsedCount(), v.getNote()
                });
            }
            rowCountLabel.setText(list.size() + " voucher");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAdd() {
        Voucher v = showForm(null);
        if (v != null) {
            try {
                controller.add(v);
                // Reset filter để chắc chắn thấy Voucher mới
                txtSearch.setText("");
                cbStatusFilter.setSelectedIndex(0);
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm voucher thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu vào DB: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn voucher để sửa!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String code = model.getValueAt(row, 1).toString();
        Voucher current = controller.getByCode(code);
        
        if (current == null) return;

        Voucher updated = showForm(current);
        if (updated != null) {
            updated.setId(id);
            try {
                controller.update(updated);
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
            }
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa voucher này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
            try {
                controller.delete(id);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }

    /* ===== FORM DIALOG ===== */
    private Voucher showForm(Voucher v0) {
        JTextField txtCode = new JTextField(v0 != null ? v0.getCode() : "");
        JComboBox<String> cbType = new JComboBox<>(new String[]{"PERCENT", "AMOUNT"});
        if (v0 != null) cbType.setSelectedItem(v0.getDiscountType());

        JTextField txtValue = new JTextField(v0 != null ? String.valueOf(v0.getDiscountValue()) : "");
        JTextField txtLimit = new JTextField(v0 != null ? String.valueOf(v0.getUsageLimit()) : "100");
        JTextField txtStart = new JTextField(v0 != null ? v0.getStartDate().format(fmt) : LocalDate.now().format(fmt));
        JTextField txtEnd   = new JTextField(v0 != null ? v0.getEndDate().format(fmt) : LocalDate.now().plusDays(30).format(fmt));
        JTextField txtNote  = new JTextField(v0 != null ? v0.getNote() : "");

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.add(new JLabel("Mã Voucher:")); p.add(txtCode);
        p.add(new JLabel("Loại giảm giá:")); p.add(cbType);
        p.add(new JLabel("Giá trị:")); p.add(txtValue);
        p.add(new JLabel("Giới hạn sử dụng:")); p.add(txtLimit);
        p.add(new JLabel("Ngày bắt đầu (dd-MM-yyyy):")); p.add(txtStart);
        p.add(new JLabel("Ngày kết thúc (dd-MM-yyyy):")); p.add(txtEnd);
        p.add(new JLabel("Ghi chú:")); p.add(txtNote);

        int result = JOptionPane.showConfirmDialog(this, p, v0 == null ? "Thêm Voucher" : "Sửa Voucher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Voucher v = new Voucher();
                v.setCode(txtCode.getText().trim().toUpperCase());
                v.setDiscountType(cbType.getSelectedItem().toString());
                v.setDiscountValue(Double.parseDouble(txtValue.getText().trim()));
                v.setUsageLimit(Integer.parseInt(txtLimit.getText().trim()));
                v.setStartDate(LocalDate.parse(txtStart.getText().trim(), fmt));
                v.setEndDate(LocalDate.parse(txtEnd.getText().trim(), fmt));
                v.setNote(txtNote.getText().trim());
                v.setUsedCount(v0 != null ? v0.getUsedCount() : 0);
                
                // QUAN TRỌNG: Gán status đúng tiếng Việt để DB chấp nhận
                v.setStatus("Còn hiệu lực");
                
                if (v.getCode().isEmpty()) throw new Exception("Mã không được để trống!");
                return v;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập vào không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : getModel().isRollover() ? base.brighter() : base);
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
