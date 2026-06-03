package org.example.view;

import org.example.controller.EmployeeController;
import org.example.controller.EmployeeShiftController;
import org.example.controller.ShiftController;
import org.example.entity.Employee;
import org.example.entity.Shift;
import org.example.entity.WorkSchedule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EmployeeShiftEditForm extends JDialog {

    // ===== COLORS =====
    private static final Color BG = new Color(0xF8FAFC);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_GRAY = new Color(0x64748B);

    private JComboBox<Employee> cbEmployee;
    private JComboBox<Shift> cbShift;
    private JTextField txtDate;

    private final EmployeeShiftController controller = new EmployeeShiftController();
    private final WorkSchedule ws;
    private final Runnable callback;

    public EmployeeShiftEditForm(WorkSchedule ws, Runnable callback) {
        this.ws = ws;
        this.callback = callback;

        setTitle("Sửa ca làm");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);

        loadEmployees();
        loadShifts();
        fillData();
    }

    /* ================= HEADER ================= */
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("✎ Cập nhật ca làm");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        p.add(title, BorderLayout.WEST);
        return p;
    }

    /* ================= FORM ================= */
    private JPanel buildForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setOpaque(false);

        cbEmployee = new JComboBox<>();
        cbShift = new JComboBox<>();
        txtDate = new JTextField();

        styleInput(cbEmployee);
        styleInput(cbShift);
        styleInput(txtDate);

        addField(form, "Nhân viên:", cbEmployee);
        addField(form, "Ca làm:", cbShift);
        addField(form, "Ngày làm:", txtDate);

        panel.add(form, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnSave = createButton("Cập nhật", BTN_BLUE);
        JButton btnCancel = createButton("Hủy", BTN_GRAY);

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        panel.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> update());
        btnCancel.addActionListener(e -> dispose());

        return panel;
    }

    /* ================= STYLE ================= */
    private void styleInput(JComponent comp) {
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E1)),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private void addField(JPanel p, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl);
        p.add(comp);
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));

        return btn;
    }

    /* ================= LOAD DATA ================= */
    private void loadEmployees() {
        new EmployeeController().getAll().forEach(cbEmployee::addItem);

        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null)
                    setText(((Employee) value).getName());
                return this;
            }
        });
    }

    private void loadShifts() {
        new ShiftController().getAll().forEach(cbShift::addItem);

        cbShift.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Shift s = (Shift) value;
                    setText(s.getName() + " (" + s.getStartTime() + "-" + s.getEndTime() + ")");
                }
                return this;
            }
        });
    }

    private void fillData() {
        for (int i = 0; i < cbEmployee.getItemCount(); i++) {
            if (cbEmployee.getItemAt(i).getId() == ws.getEmployeeId()) {
                cbEmployee.setSelectedIndex(i);
                break;
            }
        }
        cbEmployee.setEnabled(false);

        for (int i = 0; i < cbShift.getItemCount(); i++) {
            if (cbShift.getItemAt(i).getId() == ws.getShiftId()) {
                cbShift.setSelectedIndex(i);
                break;
            }
        }

        txtDate.setText(ws.getWorkDate().toString());
        txtDate.setEnabled(false);
    }

    /* ================= UPDATE ================= */
    private void update() {
        try {
            Shift newShift = (Shift) cbShift.getSelectedItem();

            controller.update(
                    ws.getEmployeeId(),
                    ws.getShiftId(),
                    newShift.getId(),
                    ws.getWorkDate().toString()
            );

            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            callback.run();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}