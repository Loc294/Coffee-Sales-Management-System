package org.example.view;

import org.example.controller.WorkScheduleController;
import org.example.entity.WorkSchedule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkSchedulePanel extends JPanel {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);   // slate-900
    private static final Color ACCENT       = new Color(0x3B82F6);   // blue-500
    private static final Color ROW_ODD      = Color.WHITE;
    private static final Color ROW_EVEN     = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);   // blue-100
    private static final Color TH_BG        = new Color(0x334155);   // slate-700
    private static final Color TH_FG        = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_GREEN    = new Color(0x22C55E);
    private static final Color BTN_AMBER    = new Color(0xF59E0B);
    private static final Color BTN_RED      = new Color(0xEF4444);
    private static final Color BTN_SLATE    = new Color(0x64748B);
    private static final Color BTN_BLUE     = new Color(0x3B82F6);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel model;
    private JLabel rowCountLabel;

    private final WorkScheduleController controller = new WorkScheduleController();

    public WorkSchedulePanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        initUI();
        loadData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void initUI() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Left: icon + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("📅");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản Lý Lịch Làm Việc");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Xem và chỉnh sửa lịch làm việc của nhân viên");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));   // slate-400

        titleBlock.add(title);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        // Right: row count badge
        rowCountLabel = new JLabel("0 bản ghi");
        rowCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rowCountLabel.setForeground(new Color(0xCBD5E1));
        rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(rowCountLabel, BorderLayout.EAST);

        return header;
    }

    // ── Center (toolbar + table) ───────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 20, 20, 20));

        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);

        return center;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton btnAdd     = createButton("＋  Thêm",       BTN_GREEN);
        JButton btnEdit    = createButton("✎  Sửa",         BTN_AMBER);
        JButton btnDelete  = createButton("✕  Xóa",         BTN_RED);
        JButton btnRefresh = createButton("↻  Làm mới",     BTN_SLATE);
        JButton btnExport  = createButton("↓  Xuất Excel",  BTN_BLUE);

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(createSeparator());
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(btnRefresh);
        toolbar.add(btnExport);

        btnAdd.addActionListener(e ->
                new EmployeeShiftForm(this::loadData).setVisible(true));
        btnEdit.addActionListener(e -> edit());
        btnDelete.addActionListener(e -> delete());
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        return toolbar;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(BORDER_COLOR);
        return sep;
    }

    // ── Table Panel ───────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] columns = {
                "STT", "Nhân Viên", "Ca Làm", "Bắt Đầu", "Kết Thúc", "Ngày Làm", "Ngày Đăng Ký"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(new Color(0x1E40AF));
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Column widths
        int[] widths = {50, 160, 130, 80, 80, 110, 150};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Center-align narrow columns
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : new int[]{0, 3, 4, 5}) {
            table.getColumnModel().getColumn(col).setCellRenderer(centerRender);
        }

        // Zebra + selection renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    setBackground(ROW_SELECTED);
                    setForeground(new Color(0x1E40AF));
                } else {
                    setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    setForeground(new Color(0x1E293B));
                }

                // Center short columns
                int[] centeredCols = {0, 3, 4, 5};
                boolean center = false;
                for (int c : centeredCols) if (c == col) { center = true; break; }
                setHorizontalAlignment(center ? CENTER : LEFT);

                return this;
            }
        });

        // Table header
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(TH_BG);
        th.setForeground(TH_FG);
        th.setPreferredSize(new Dimension(0, 40));
        th.setReorderingAllowed(false);
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBackground(TH_BG);
                setForeground(TH_FG);
                setFont(FONT_HEADER);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return this;
            }
        });

        // Hover highlight
        table.addMouseMotionListener(new MouseAdapter() {
            int hovered = -1;
            @Override public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hovered) { hovered = row; table.repaint(); }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);

        // Rounded card wrapper
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        card.add(scroll, BorderLayout.CENTER);

        // Wrap in a plain scroll pane without double border
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Button factory
    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover()
                        ? base.brighter()
                        : base);
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
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setMargin(new Insets(0, 10, 0, 10));
        return btn;
    }

    // ── Load data ─────────────────────────────────────────────────────────────
    private void loadData() {
        model.setRowCount(0);

        List<WorkSchedule> list = controller.getAll();

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter regFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int stt = 1;
        for (WorkSchedule ws : list) {
            model.addRow(new Object[]{
                    stt++,
                    ws.getEmployeeName(),
                    ws.getShiftName(),
                    ws.getStartTime().format(timeFmt),
                    ws.getEndTime().format(timeFmt),
                    ws.getWorkDate().format(dateFmt),
                    ws.getRegisterDate() == null
                            ? "—"
                            : ws.getRegisterDate().format(regFmt)
            });
        }

        rowCountLabel.setText(list.size() + " bản ghi");
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void delete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showInfo("Vui lòng chọn 1 dòng để xóa.");
            return;
        }

        WorkSchedule ws = controller.getAll().get(row);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa lịch làm việc này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(
                    ws.getEmployeeId(),
                    ws.getShiftId(),
                    ws.getWorkDate().toString()
            );
            loadData();
        }
    }

    // ── Edit ──────────────────────────────────────────────────────────────────
    private void edit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showInfo("Vui lòng chọn 1 dòng để sửa.");
            return;
        }

        WorkSchedule ws = controller.getAll().get(row);
        new EmployeeShiftEditForm(ws, this::loadData).setVisible(true);
    }

    // ── Export Excel ──────────────────────────────────────────────────────────
    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new File("lich_lam_viec.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (
                org.apache.poi.ss.usermodel.Workbook wb =
                        new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(file)
        ) {
            var sheet = wb.createSheet("Lịch Làm Việc");

            // Header style
            var headerStyle = wb.createCellStyle();
            var hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(
                    new org.apache.poi.xssf.usermodel.XSSFColor(
                            new byte[]{0x33, 0x41, 0x55}, null));
            headerStyle.setFillPattern(
                    org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(
                    org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            String[] cols = {"STT","Nhân Viên","Ca Làm","Bắt Đầu","Kết Thúc","Ngày Làm","Ngày Đăng Ký"};
            var hRow = sheet.createRow(0);
            hRow.setHeightInPoints(22);
            for (int i = 0; i < cols.length; i++) {
                var cell = hRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            List<WorkSchedule> list = controller.getAll();
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter regFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            int ri = 1, stt = 1;
            for (WorkSchedule ws : list) {
                var r = sheet.createRow(ri++);
                r.setHeightInPoints(18);
                r.createCell(0).setCellValue(stt++);
                r.createCell(1).setCellValue(ws.getEmployeeName());
                r.createCell(2).setCellValue(ws.getShiftName());
                r.createCell(3).setCellValue(ws.getStartTime().format(timeFmt));
                r.createCell(4).setCellValue(ws.getEndTime().format(timeFmt));
                r.createCell(5).setCellValue(ws.getWorkDate().format(dateFmt));
                r.createCell(6).setCellValue(
                        ws.getRegisterDate() == null ? "" : ws.getRegisterDate().format(regFmt));
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(fos);
            JOptionPane.showMessageDialog(this,
                    "✅  Xuất Excel thành công!\n" + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "❌  Lỗi khi xuất file:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ── Helper ────────────────────────────────────────────────────────────────
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}