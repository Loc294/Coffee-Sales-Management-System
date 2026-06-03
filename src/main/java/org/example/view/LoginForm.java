package org.example.view;

import org.example.controller.AuthController;
import org.example.entity.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class LoginForm extends JFrame {

    private JTextField username;
    private JPasswordField password;
    private JButton btnLogin;

    private final AuthController authController = new AuthController();

    public LoginForm() {
//        AuthService authService = new AuthServiceImpl();

        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 245, 250));
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        add(container);

        /* ================= HEADER (LOGO + TITLE) ================= */
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 250));

        // Logo
        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/logoHighland.png"))

        );
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        // Title
        JLabel title = new JLabel("QUẢN LÝ BÁN COFFEE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(50, 50, 70));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        headerPanel.add(lblLogo, BorderLayout.CENTER);
        headerPanel.add(title, BorderLayout.SOUTH);

        container.add(headerPanel, BorderLayout.NORTH);

        /* ================= FORM ================= */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        container.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        username = new JTextField(18);
        username.setPreferredSize(new Dimension(250, 35));
        formPanel.add(username, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        password = new JPasswordField(18);
        password.setPreferredSize(new Dimension(250, 35));
        formPanel.add(password, gbc);

        // Login Button
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(250, 40));

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(60, 120, 170));
            }
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(70, 130, 180));
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gbc);

        // Register link
        var linkRegister = new JLabel("<html><u>Chưa có tài khoản? Đăng ký ngay</u></html>");
        linkRegister.setForeground(new Color(70, 130, 180));
        linkRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        linkRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });

        gbc.gridy = 3;
        formPanel.add(linkRegister, gbc);

        btnLogin.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        try {
            Account account = authController.onLogin(
                    username.getText().trim(),
                    new String(password.getPassword()).trim()
            );

            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            new MainFrame(
                    account.getUsername(),
                    account.getRole()
            ).setVisible(true);

            dispose();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}

