package org.example.view;

import org.example.controller.EmployeeController;
import org.example.controller.EmployeeShiftController;
import org.example.controller.ShiftController;
import org.example.entity.Employee;
import org.example.entity.Shift;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EmployeeShiftForm extends JDialog {

    private JComboBox<Employee> cbEmployee;
    private JComboBox<Shift> cbShift;
    private JTextField txtDate;

    private final EmployeeShiftController controller =
            new EmployeeShiftController();

    public EmployeeShiftForm(Runnable callback) {

        setTitle("Gán ca cho nhân viên");
        setSize(360, 230);
        setLocationRelativeTo(null);
        setModal(true);

        initUI(callback);
    }

    private void initUI(Runnable callback) {

        setLayout(new GridLayout(4, 2, 10, 10));

        cbEmployee = new JComboBox<>();
        cbShift = new JComboBox<>();
        txtDate = new JTextField(LocalDate.now().toString());

        loadEmployees();
        loadShifts();

        JButton btnSave = new JButton("Gán ca");
        JButton btnCancel = new JButton("Hủy");

        add(new JLabel("Nhân viên"));
        add(cbEmployee);

        add(new JLabel("Ca làm"));
        add(cbShift);

        add(new JLabel("Ngày làm (yyyy-MM-dd)"));
        add(txtDate);

        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> save(callback));
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadEmployees() {
        new EmployeeController().getAll().forEach(cbEmployee::addItem);

        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null) {
                    setText(((Employee) value).getName());
                }
                return this;
            }
        });
    }

    private void loadShifts() {
        new ShiftController().getAll().forEach(cbShift::addItem);

        cbShift.setRenderer(new DefaultListCellRenderer() {
            @Override
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

    private void save(Runnable callback) {

        try {
            Employee emp = (Employee) cbEmployee.getSelectedItem();
            Shift shift = (Shift) cbShift.getSelectedItem();
            LocalDate date = LocalDate.parse(txtDate.getText().trim());

            controller.assignShift(emp.getId(), shift.getId(), date);

            JOptionPane.showMessageDialog(this, "Gán ca thành công");
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
