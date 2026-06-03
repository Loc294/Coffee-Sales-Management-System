package org.example.view;

import org.example.controller.AccountController;
import org.example.entity.Account;
import org.example.event.DataChangeEventBus;
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

public class AccountManagementPanel extends JPanel {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);
    private static final Color ROW_ODD      = Color.WHITE;
    private static final Color ROW_EVEN     = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);
    private static final Color TH_BG        = new Color(0x334155);
    private static final Color TH_FG        = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_GREEN  = new Color(0x22C55E);
    private static final Color BTN_AMBER  = new Color(0xF59E0B);
    private static final Color BTN_RED    = new Color(0xEF4444);
    private static final Color BTN_SLATE  = new Color(0x64748B);
    private static final Color BTN_BLUE   = new Color(0x3B82F6);

    private static final Color BADGE_ACTIVE = new Color(0xDCFCE7);
    private static final Color BADGE_ACTIVE_FG = new Color(0x166534);
    private static final Color BADGE_LOCKED = new Color(0xFEE2E2);
    private static final Color BADGE_LOCKED_FG = new Color(0x991B1B);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    // ── Fields ────────────────────────────────────────────────────────────────
    private final AccountController controller = new AccountController();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private JLabel rowCountLabel;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        initUI();
        loadData();
        DataChangeEventBus.onRegister(this::loadData);
    }

    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Left
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("👤");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản Lý Tài Khoản");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Thêm, sửa, xóa và phân quyền tài khoản đăng nhập hệ thống");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(title);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        // Right: row count
        rowCountLabel = new JLabel("0 tài khoản");
        rowCountLabel.setFont(FONT_BOLD);
        rowCountLabel.setForeground(new Color(0xCBD5E1));
        rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(rowCountLabel, BorderLayout.EAST);

        return header;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 20, 20, 20));

        center.add(buildControlBar(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);

        return center;
    }

    // ── Control bar ──────────────────────────────────────────────────────────
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Search group
        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 36));
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm username...");

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Khóa"});
        cbStatus.setFont(FONT_BODY);
        cbStatus.setPreferredSize(new Dimension(130, 36));

        JButton btnSearch = createButton("🔍  Tìm", BTN_BLUE);

        searchGroup.add(txtSearch);
        searchGroup.add(cbStatus);
        searchGroup.add(btnSearch);

        // Action buttons
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        JButton btnAdd     = createButton("＋  Thêm",      BTN_GREEN);
        JButton btnEdit    = createButton("✎  Sửa",        BTN_AMBER);
        JButton btnDelete  = createButton("✕  Xóa",        BTN_RED);
        JButton btnRefresh = createButton("↻  Làm mới",    BTN_SLATE);
        JButton btnExport  = createButton("↓  Excel",      BTN_BLUE);

        actionGroup.add(btnAdd);
        actionGroup.add(btnEdit);
        actionGroup.add(btnDelete);
        actionGroup.add(btnRefresh);
        actionGroup.add(btnExport);

        bar.add(searchGroup,  BorderLayout.WEST);
        bar.add(actionGroup,  BorderLayout.EAST);

        btnSearch.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cbStatus.setSelectedIndex(0); loadData(); });
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteAccount());
        btnExport.addActionListener(e -> ExportToExcel.export(table, "DanhSachTaiKhoan.xlsx"));

        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "STT", "Username", "Mật Khẩu", "Phân Quyền", "Trạng Thái"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(new Color(0x1E40AF));
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                if (col == 4 && value != null) {
                    boolean active = value.toString().equals("Hoạt động");
                    JLabel badge = new JLabel(active ? "● Hoạt động" : "● Khóa");
                    badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(CENTER);
                    badge.setBackground(isSelected ? ROW_SELECTED : (active ? BADGE_ACTIVE : BADGE_LOCKED));
                    badge.setForeground(active ? BADGE_ACTIVE_FG : BADGE_LOCKED_FG);
                    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
                    return badge;
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col); // Đã sửa 'v' thành 'value'
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setBackground(isSelected ? ROW_SELECTED : (row % 2 == 0 ? ROW_ODD : ROW_EVEN));
                setForeground(isSelected ? new Color(0x1E40AF) : new Color(0x1E293B));
                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(TH_BG);
        th.setForeground(TH_FG);
        th.setPreferredSize(new Dimension(0, 40));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Account> list = controller.search(txtSearch.getText(), Objects.requireNonNull(cbStatus.getSelectedItem()).toString());
        int stt = 1;
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(), stt++, a.getUsername(), a.getPassword(), a.getRole(), a.isActive() ? "Hoạt động" : "Khóa"
            });
        }
        rowCountLabel.setText(list.size() + " tài khoản");
    }

    private void openAddDialog() {
        JTextField    user   = new JTextField();
        JPasswordField pass  = new JPasswordField();
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        JCheckBox active = new JCheckBox("Hoạt động", true);

        JPanel p = createForm();
        addField(p, "Username:",   user);
        addField(p, "Password:",   pass);
        addField(p, "Phân quyền:", role);
        addField(p, "Trạng thái:", active);

        if (JOptionPane.showConfirmDialog(this, p, "＋ Thêm tài khoản", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
            controller.add(user.getText(), new String(pass.getPassword()), Objects.requireNonNull(role.getSelectedItem()).toString(), active.isSelected());
            loadData();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int id       = (int) tableModel.getValueAt(modelRow, 0);
        Account acc  = controller.findByUsername(tableModel.getValueAt(modelRow, 2).toString());

        JTextField    user  = new JTextField(acc.getUsername());
        JPasswordField pass = new JPasswordField(acc.getPassword());
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "STAFF", "USER"});
        role.setSelectedItem(acc.getRole());
        JCheckBox active = new JCheckBox("Hoạt động", acc.isActive());

        JPanel p = createForm();
        addField(p, "Username:",   user);
        addField(p, "Password:",   pass);
        addField(p, "Phân quyền:", role);
        addField(p, "Trạng thái:", active);

        if (JOptionPane.showConfirmDialog(this, p, "✎ Sửa tài khoản", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
            controller.update(id, user.getText(), new String(pass.getPassword()), Objects.requireNonNull(role.getSelectedItem()).toString(), active.isSelected());
            loadData();
        }
    }

    private void deleteAccount() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int id       = (int) tableModel.getValueAt(modelRow, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData();
        }
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
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 12));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(Color.WHITE);
        return p;
    }

    private void addField(JPanel p, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        p.add(lbl);
        p.add(comp);
    }
}
