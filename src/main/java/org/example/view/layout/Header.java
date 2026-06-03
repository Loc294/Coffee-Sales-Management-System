package org.example.view.layout;

import org.example.view.dialog.ProfileDialog;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {

    private final JLabel moduleLabel;
    private final JLabel userInfoLabel;

    private final JButton btnAdd;
    private final JButton btnEdit;

    // Lưu user hiện tại (tránh bug)
    private String currentUsername;
    private String currentRole;

    public Header(String username, String role) {

        this.currentUsername = username;
        this.currentRole = role;

        setPreferredSize(new Dimension(0, 80));
        setBackground(new Color(248, 249, 255));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(230, 232, 245))
        );

        // ===== Module title (trái) =====
        moduleLabel = new JLabel("Lễ Tân & Tiếp Đón");
        moduleLabel.setForeground(new Color(72, 69, 130));
        moduleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        moduleLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));

        // ===== Cụm phải =====
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 24));

        // ===== Hàng nút =====
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionRow.setOpaque(false);

        btnAdd = createPrimaryButton("+ Thêm");
        btnEdit = createGhostButton("✏ Sửa");

        actionRow.add(btnAdd);
        actionRow.add(btnEdit);

        // ===== Hàng user info =====
        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));
        userRow.setOpaque(false);

        userInfoLabel = new JLabel("👤 " + username + " (" + role + ")");
        userInfoLabel.setForeground(new Color(100, 102, 130));
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userInfoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ===== CLICK MỞ PROFILE =====
        userInfoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ProfileDialog(currentUsername, currentRole).setVisible(true);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                userInfoLabel.setForeground(new Color(113, 99, 248));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                userInfoLabel.setForeground(new Color(100, 102, 130));
            }
        });

        userRow.add(userInfoLabel);

        rightPanel.add(actionRow);
        rightPanel.add(userRow);

        add(moduleLabel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    /* ===== BUTTON STYLE ===== */

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(113, 99, 248));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 36));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 85, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(113, 99, 248));
            }
        });

        return btn;
    }

    private JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(113, 99, 248));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 99, 248), 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 36));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 240, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    /* ===== API ===== */

    public void setModuleTitle(String title) {
        moduleLabel.setText(title);
    }

    public void setUserInfo(String username, String role) {
        this.currentUsername = username;
        this.currentRole = role;
        userInfoLabel.setText("👤 " + username + " (" + role + ")");
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnEdit() {
        return btnEdit;
    }
}