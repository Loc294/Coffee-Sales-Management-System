package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.EmployeeController;
import org.example.entity.Account;
import org.example.entity.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EmployeeForm extends JDialog {

    private JTextField txtName, txtPhone;
    private JComboBox<String> cbPosition;

    private final EmployeeController controller = new EmployeeController();
    private final Employee employee;
    private final Runnable callback;

    public EmployeeForm(Employee employee, Runnable callback) {
        this.employee = employee;
        this.callback = callback;

        setTitle(employee == null ? "Add Employee" : "Edit Employee");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
        if (employee != null) fillData();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));

        txtName = new JTextField();
        txtPhone = new JTextField();

        cbPosition = new JComboBox<>(new String[]{"Staff"});

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        add(new JLabel("Name"));
        add(txtName);

        add(new JLabel("Phone"));
        add(txtPhone);

        add(new JLabel("Position"));
        add(cbPosition);

        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(employee.getName());
        txtPhone.setText(employee.getPhone());
        cbPosition.setSelectedItem(employee.getPosition());
    }


    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String position = Objects.requireNonNull(cbPosition.getSelectedItem()).toString();

        if (employee == null) {
            controller.create(name, phone, position, null);
        } else {
            employee.setName(name);
            employee.setPhone(phone);
            employee.setPosition(position);
            employee.setAccountId(null);
            controller.update(employee);
        }

        callback.run();
        dispose();
    }
}