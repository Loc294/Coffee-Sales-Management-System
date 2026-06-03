package org.example.view.layout;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Sidebar extends JScrollPane {

    private final JPanel container = new JPanel();
    private final JPanel menuPanel = new JPanel();

    private final Map<String, JButton> menuMap = new LinkedHashMap<>();
    private final Map<String, List<String>> rolePermissions = new HashMap<>();

    private static final Color BG        = new Color(30, 35, 50);
    private static final Color BG_ITEM   = new Color(40, 45, 65);
    private static final Color BG_HOVER  = new Color(60, 70, 100);
    private static final Color BG_ACTIVE = new Color(85, 120, 200);
    private static final Color ACCENT    = new Color(0, 180, 255);

    public Sidebar(String role) {

        // FIX: chuẩn hóa role tránh lỗi "staff" vs "STAFF"
        String normalizedRole = (role != null) ? role.toUpperCase().trim() : "";

        container.setLayout(new BorderLayout());
        container.setBackground(BG);
        container.add(createHeader(), BorderLayout.NORTH);

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        container.add(menuPanel, BorderLayout.CENTER);

        setViewportView(container);
        setPreferredSize(new Dimension(220, 0));
        setBorder(null);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        initPermissions();

        List<String> menus = rolePermissions.getOrDefault(normalizedRole, new ArrayList<>());

        // DEBUG - xóa sau khi fix xong
        System.out.println(">>> Sidebar role: '" + normalizedRole + "' | menus: " + menus);

        for (String menu : menus) {
            addMenu(menu);
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));

        JLabel icon = new JLabel("☕");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel title = new JLabel("  Coffee Admin");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.add(icon);
        row.add(title);

        header.add(row, BorderLayout.CENTER);

        // Divider
        JPanel divider = new JPanel();
        divider.setBackground(new Color(60, 70, 100));
        divider.setPreferredSize(new Dimension(0, 1));
        header.add(divider, BorderLayout.SOUTH);

        return header;
    }

    private void initPermissions() {
        rolePermissions.put("ADMIN", Arrays.asList(
                "Thống Kê",
                "Quản Lý Tài Khoản",
                "Quản Lý Nhân Viên",
                "Quản Lý Khách Hàng",
                "Công Thức Pha Chế",
                "Quản Lý Ca Làm",
                "Quản Lý Lịch Làm",
                "Sản Phẩm",
                "Quản Lý Tồn Kho",
                "Đơn Hàng",
                "Voucher"

        ));

        rolePermissions.put("STAFF", Arrays.asList(
                "Sản Phẩm",
                "Quản Lý Nhân Viên",
                "Công Thức Pha Chế",
                "Voucher",
                "Quản Lý Lịch Làm",
                "Đơn Hàng"
        ));
    }

    private void addMenu(String title) {
        JButton btn = new JButton("  " + title);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_ITEM);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(BG_ACTIVE))
                    btn.setBackground(BG_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(BG_ACTIVE))
                    btn.setBackground(BG_ITEM);
            }
        });

        menuMap.put(title, btn);
        menuPanel.add(btn);
        menuPanel.add(Box.createVerticalStrut(4));
    }

    // FIX: nhận đúng menu label (không phải moduleTitle)
    public void setActiveMenu(String menuLabel) {
        menuMap.forEach((k, v) -> {
            boolean active = k.equals(menuLabel);
            v.setBackground(active ? BG_ACTIVE : BG_ITEM);
            if (active) {
                v.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                        BorderFactory.createEmptyBorder(10, 11, 10, 10)
                ));
            } else {
                v.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
            }
        });
    }

    public JButton getMenu(String title) {
        return menuMap.get(title);
    }

    public Set<String> getAvailableMenus() {
        return menuMap.keySet();
    }
}