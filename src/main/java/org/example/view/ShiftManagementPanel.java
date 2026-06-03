package org.example.view;

import org.example.controller.ShiftController;
import org.example.entity.Shift;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShiftManagementPanel extends JPanel {

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
    private static final Color BTN_SLATE = new Color(0x64748B);
    private static final Color BTN_BLUE = new Color(0x3B82F6);

    // ===== FONTS =====
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel model;
    private JLabel rowCountLabel;

    private final ShiftController controller = new ShiftController();

    public ShiftManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadData();
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("⏰");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JLabel title = new JLabel("Quản Lý Ca Làm");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        left.add(icon);
        left.add(title);

        rowCountLabel = new JLabel("0 ca");
        rowCountLabel.setForeground(Color.WHITE);

        header.add(left, BorderLayout.WEST);
        header.add(rowCountLabel, BorderLayout.EAST);

        return header;
    }

    // ===== CENTER =====
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(16, 20, 20, 20));

        panel.add(buildControlBar(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);

        return panel;
    }

    // ===== CONTROL BAR =====
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setOpaque(false);

        JButton btnAdd = createButton("＋ Thêm", BTN_GREEN);
        JButton btnEdit = createButton("✎ Sửa", BTN_AMBER);
        JButton btnDelete = createButton("✕ Xóa", BTN_RED);
        JButton btnRefresh = createButton("↻ Refresh", BTN_SLATE);
        JButton btnExport = createButton("↓ Excel", BTN_BLUE);

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(btnRefresh);
        bar.add(btnExport);

        // Events
        btnAdd.addActionListener(e ->
                new ShiftForm(null, this::loadData).setVisible(true)
        );

        btnEdit.addActionListener(e -> editShift());
        btnDelete.addActionListener(e -> deleteShift());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        return bar;
    }

    // ===== TABLE =====
    private JScrollPane buildTable() {

        model = new DefaultTableModel(
                new String[]{"STT", "Tên ca", "Bắt đầu", "Kết thúc", "Ngày tạo", "Cập nhật"},
                0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ROW_SELECTED);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowVerticalLines(false);

        table.setAutoCreateRowSorter(true);

        // Zebra + style
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    setBackground(ROW_SELECTED);
                } else {
                    setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                }
                return this;
            }
        });

        // Header
        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0x334155));
        th.setForeground(Color.WHITE);
        th.setFont(FONT_BOLD);
        th.setPreferredSize(new Dimension(0, 40));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        return scroll;
    }

    // ===== LOAD DATA =====
    private void loadData() {
        model.setRowCount(0);

        List<Shift> list = controller.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int stt = 1;
        for (Shift s : list) {
            model.addRow(new Object[]{
                    stt++,
                    s.getName(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getCreatedTime() == null ? "" : s.getCreatedTime().format(fmt),
                    s.getUpdateTime() == null ? "" : s.getUpdateTime().format(fmt)
            });
        }

        rowCountLabel.setText(list.size() + " ca làm");
    }

    // ===== EDIT =====
    private void editShift() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        Shift shift = controller.getAll().get(modelRow);

        new ShiftForm(shift, this::loadData).setVisible(true);
    }

    // ===== DELETE =====
    private void deleteShift() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(row);
            int id = controller.getAll().get(modelRow).getId();
            controller.delete(id);
            loadData();
        }
    }

    // ===== EXPORT =====
    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("shift.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (
                org.apache.poi.ss.usermodel.Workbook wb =
                        new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())
        ) {
            var sheet = wb.createSheet("Shift");

            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.createRow(0).createCell(i).setCellValue(model.getColumnName(i));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                var row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.createCell(j).setCellValue(model.getValueAt(i, j).toString());
                }
            }

            wb.write(fos);
            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file!");
        }
    }

    // ===== BUTTON STYLE =====
    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(getModel().isPressed() ? base.darker() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }
}