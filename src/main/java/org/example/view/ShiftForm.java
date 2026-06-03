package org.example.view;

import org.example.controller.ShiftController;
import org.example.entity.Shift;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class ShiftForm extends JDialog {

    private JTextField txtName, txtStart, txtEnd;
    private final Shift shift;
    private final Runnable callback;
    private final ShiftController controller = new ShiftController();

    public ShiftForm(Shift shift, Runnable callback) {
        this.shift = shift;
        this.callback = callback;

        setTitle(shift == null ? "Add Shift" : "Edit Shift");
        setSize(320, 220);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
        if (shift != null) fillData();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));

        txtName = new JTextField();
        txtStart = new JTextField("07:00");
        txtEnd = new JTextField("11:00");

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        add(new JLabel("Tên ca"));
        add(txtName);

        add(new JLabel("Giờ bắt đầu (HH:mm)"));
        add(txtStart);

        add(new JLabel("Giờ kết thúc (HH:mm)"));
        add(txtEnd);

        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(shift.getName());
        txtStart.setText(shift.getStartTime().toString());
        txtEnd.setText(shift.getEndTime().toString());
    }

    private void save() {
        try {
            String name = txtName.getText().trim();
            LocalTime start = LocalTime.parse(txtStart.getText().trim());
            LocalTime end = LocalTime.parse(txtEnd.getText().trim());

            if (shift == null) {
                controller.create(name, start, end);
            } else {
                shift.setName(name);
                shift.setStartTime(start);
                shift.setEndTime(end);
                controller.update(shift);
            }

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
