package org.example.view.dialog;

import org.example.view.LoginForm;

import javax.swing.*;
import java.awt.*;

public class ProfileDialog extends JDialog {

    public ProfileDialog(String username, String role) {

        setTitle("Profile");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== CONTAINER =====
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== AVATAR =====
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel(role.toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(108, 117, 125));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(avatar);
        container.add(Box.createVerticalStrut(10));
        container.add(nameLabel);
        container.add(Box.createVerticalStrut(5));
        container.add(roleLabel);
        container.add(Box.createVerticalStrut(20));

        // ===== INFO CARD =====
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(new Color(248, 249, 255));
        infoCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        infoCard.add(createInfoRow("Username", username));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(createInfoRow("Role", role));
        infoCard.add(Box.createVerticalStrut(10));
        infoCard.add(createInfoRow("Trạng thái", "Đang hoạt động"));

        container.add(infoCard);
        container.add(Box.createVerticalStrut(25));

        // ===== BUTTON =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnClose = new JButton("Đóng");
        styleButton(btnClose, new Color(108, 117, 125));

        JButton btnLogout = new JButton("Đăng xuất");
        styleButton(btnLogout, new Color(220, 53, 69));

        buttonPanel.add(btnClose);
        buttonPanel.add(btnLogout);

        container.add(buttonPanel);

        add(container, BorderLayout.CENTER);

        // ===== ACTION =====
        btnClose.addActionListener(e -> dispose());

        btnLogout.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
            new LoginForm().setVisible(true);
        });
    }

    // ===== COMPONENT HIỂN THỊ INFO =====
    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(248, 249, 255));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(108, 117, 125));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));

        row.add(lbl, BorderLayout.NORTH);
        row.add(val, BorderLayout.CENTER);

        return row;
    }

    // ===== STYLE BUTTON =====
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}