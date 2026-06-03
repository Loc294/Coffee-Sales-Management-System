package org.example.view;

import org.example.session.UserSession;
import org.example.view.*;
import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.Sidebar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Header header;
    private final Sidebar sideBar;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    /* ===== CARD KEYS ===== */
    private static final String SCREEN_STATISTIC     = "statistic";
    private static final String SCREEN_ACCOUNTS      = "accounts";
    private static final String SCREEN_CUSTOMERS     = "customers";
    private static final String SCREEN_EMPLOYEES     = "employees";
    private static final String SCREEN_SHIFTS        = "shifts";
    private static final String SCREEN_WORK_SCHEDULE = "work_schedule";
    private static final String SCREEN_PRODUCTS      = "products";
    private static final String SCREEN_VOUCHERS      = "vouches";
    private static final String SCREEN_SETTINGS      = "settings";
    private static final String SCREEN_INVENTORY     = "inventory";
    private static final String SCREEN_RECIPE        = "recipe";
    private static final String SCREEN_ORDER         = "order";

    private String currentModuleTitle = "";

    public MainFrame(String username, String role) {

        /* ===== SESSION ===== */
        UserSession.getInstance().setUsername(username);
        UserSession.getInstance().setRole(role);

        /* ===== FRAME ===== */
        setTitle("Hệ Thống Quản Lý Bán Cà Phê");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        /* ===== COMPONENTS ===== */
        header = new Header(username, role);
        sideBar = new Sidebar(role);

        /* ===== CONTENT PANEL ===== */
        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        bgContent.add(contentPanel, BorderLayout.CENTER);

        /* ===== LAYOUT ===== */
        add(sideBar,           BorderLayout.WEST);
        add(header,            BorderLayout.NORTH);
        add(bgContent,         BorderLayout.CENTER);
        add(new Footer(),      BorderLayout.SOUTH);

        /* ===== INIT ===== */
        initScreens();
        initMenuActions();
        initHeaderActions();
    }

    /* ================= SCREENS ================= */
    private void initScreens() {

        contentPanel.add(new AccountManagementPanel(),   SCREEN_ACCOUNTS);
        contentPanel.add(new CustomerManagementPanel(),  SCREEN_CUSTOMERS);
        contentPanel.add(new EmployeeManagementPanel(),  SCREEN_EMPLOYEES);
        contentPanel.add(new ShiftManagementPanel(),     SCREEN_SHIFTS);
        contentPanel.add(new WorkSchedulePanel(),        SCREEN_WORK_SCHEDULE);
        contentPanel.add(new ProductManagementPanel(),   SCREEN_PRODUCTS);
        contentPanel.add(new VoucherManagementPanel(),   SCREEN_VOUCHERS);
        contentPanel.add(new StatisticPanel(),           SCREEN_STATISTIC);
        contentPanel.add(new InventoryManagementPanel(), SCREEN_INVENTORY);
        contentPanel.add(new RecipeManagementPanel(),    SCREEN_RECIPE);
        contentPanel.add(new OrderManagementPanel(),     SCREEN_ORDER);
        contentPanel.add(createSettingScreen(),          SCREEN_SETTINGS);

        /* ===== MÀN HÌNH MẶC ĐỊNH THEO ROLE ===== */
        if (UserSession.getInstance().isAdmin()) {
            showScreen(SCREEN_STATISTIC, "Thống Kê Tổng Quan", "Thống Kê");
        } else {
            showScreen(SCREEN_PRODUCTS, "Sản Phẩm", "Sản Phẩm");
        }
    }

    /* ================= MENU ACTIONS ================= */
    private void initMenuActions() {
        bindMenu("Thống Kê",          () -> showScreen(SCREEN_STATISTIC,     "Thống Kê Tổng Quan",  "Thống Kê"));
        bindMenu("Quản Lý Tài Khoản", () -> showScreen(SCREEN_ACCOUNTS,      "Quản Lý Tài Khoản",   "Quản Lý Tài Khoản"));
        bindMenu("Quản Lý Khách Hàng",() -> showScreen(SCREEN_CUSTOMERS,     "Quản Lý Khách Hàng",  "Quản Lý Khách Hàng"));
        bindMenu("Quản Lý Nhân Viên", () -> showScreen(SCREEN_EMPLOYEES,     "Quản Lý Nhân Viên",   "Quản Lý Nhân Viên"));
        bindMenu("Quản Lý Ca Làm",    () -> showScreen(SCREEN_SHIFTS,        "Quản Lý Ca Làm",      "Quản Lý Ca Làm"));
        bindMenu("Quản Lý Lịch Làm",  () -> showScreen(SCREEN_WORK_SCHEDULE, "Quản Lý Lịch Làm",   "Quản Lý Lịch Làm"));
        bindMenu("Sản Phẩm",          () -> showScreen(SCREEN_PRODUCTS,      "Sản Phẩm",            "Sản Phẩm"));
        bindMenu("Voucher",           () -> showScreen(SCREEN_VOUCHERS,      "Voucher",             "Voucher"));
        bindMenu("Cài Đặt",           () -> showScreen(SCREEN_SETTINGS,      "Cài Đặt Hệ Thống",    "Cài Đặt"));
        bindMenu("Quản Lý Tồn Kho",   () -> showScreen(SCREEN_INVENTORY,     "Quản Lý Tồn Kho",     "Quản Lý Tồn Kho"));
        bindMenu("Công Thức Pha Chế", () -> showScreen(SCREEN_RECIPE,        "Công Thức Pha Chế",   "Công Thức Pha Chế"));
        bindMenu("Đơn Hàng",          () -> showScreen(SCREEN_ORDER,         "Đơn Hàng",            "Đơn Hàng"));

        bindMenu("Đăng xuất", () -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Đăng xuất",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                UserSession.getInstance().clear();
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    /* ===== HELPER: gắn action vào menu button nếu tồn tại ===== */
    private void bindMenu(String menuLabel, Runnable action) {
        JButton btn = sideBar.getMenu(menuLabel);
        if (btn != null) {
            btn.addActionListener(e -> action.run());
        }
    }

    /* ================= HEADER ACTIONS ================= */
    private void initHeaderActions() {
        header.getBtnAdd().addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Thêm tại: " + currentModuleTitle));

        header.getBtnEdit().addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Sửa tại: " + currentModuleTitle));
    }

    /* ================= SHOW SCREEN ================= */
    private void showScreen(String cardKey, String moduleTitle, String menuLabel) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(menuLabel);
        cardLayout.show(contentPanel, cardKey);

        boolean isStatistic = SCREEN_STATISTIC.equals(cardKey);
        header.getBtnAdd().setVisible(!isStatistic);
        header.getBtnEdit().setVisible(!isStatistic);
    }

    /* ================= SETTING SCREEN ================= */
    private JPanel createSettingScreen() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 22, 22));

        JLabel lbl = new JLabel("Cài Đặt Hệ Thống", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(120, 120, 160));

        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    /* ================= ROUNDED PANEL ================= */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, radius, radius);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);
        }
    }

    /* ================= TEST ================= */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame("admin", "ADMIN").setVisible(true)
        );
    }
}