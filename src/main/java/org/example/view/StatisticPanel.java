package org.example.view;

import org.example.controller.StatisticController;
import org.example.event.DataChangeEventBus;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class StatisticPanel extends JPanel
        implements DataChangeEventBus.DataChangeListener {

    private JLabel lblCustomer;
    private JLabel lblEmployee;
    private JLabel lblRevenue;
    private JLabel lblStock;
    private JLabel lblExported;

    private final StatisticController reportController = new StatisticController();
    private ChartPanel chartPanel; // Biến toàn cục để có thể cập nhật

    public StatisticPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 255));

        initUI();
        loadData();

        DataChangeEventBus.onRegister(this);
    }

    /* ================= UI ================= */
    private void initUI() {

        JLabel title = new JLabel("📊 Dashboard thống kê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 70));
        add(title, BorderLayout.NORTH);

        /* ===== CARD ===== */
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);

        lblCustomer = createValueLabel();
        lblEmployee = createValueLabel();
        lblRevenue = createValueLabel();
        lblStock = createValueLabel();
        lblExported = createValueLabel();

        grid.add(createCard("Khách hàng", "👤", lblCustomer, new Color(52,152,219)));
        grid.add(createCard("Nhân viên", "👨‍💼", lblEmployee, new Color(46,204,113)));
        grid.add(createCard("Doanh thu tháng này", "💰", lblRevenue, new Color(241,196,15)));
        grid.add(createCard("Tồn kho", "📦", lblStock, new Color(230,126,34)));
        grid.add(createCard("Xuất kho", "🚚", lblExported, new Color(231,76,60)));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        grid.setPreferredSize(new Dimension(700, 260));
        wrapper.add(grid);

        /* ===== CHART ===== */
        JPanel chartContainer = createRevenueChart();

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        center.add(wrapper, BorderLayout.NORTH);
        center.add(chartContainer, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        try {
            lblCustomer.setText(formatNumber(reportController.getCustomerCount()));
            lblEmployee.setText(formatNumber(reportController.getEmployeeCount()));
            lblRevenue.setText(formatCurrency(reportController.getMonthlyRevenue()));
            lblStock.setText(formatNumber(reportController.getTotalInventory()));
            lblExported.setText(formatNumber(reportController.getExportedTotal()));

            // Tải dữ liệu cho biểu đồ
            updateChartData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu thống kê: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateChartData() {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) chartPanel.getChart().getCategoryPlot().getDataset();
        dataset.clear(); // Xóa dữ liệu cũ

        Map<Integer, Double> revenueData = reportController.getRevenueByMonths();
        for (Map.Entry<Integer, Double> entry : revenueData.entrySet()) {
            String month = "T" + entry.getKey();
            dataset.addValue(entry.getValue(), "Doanh thu", month);
        }
    }

    /* ================= FORMAT ================= */
    private String formatNumber(int value) {
        return String.format("%,d", value);
    }

    private String formatCurrency(double value) {
        return String.format("%,.0f VNĐ", value);
    }

    /* ================= EVENT ================= */
    @Override
    public void onDataChanged() {
        reload();
    }

    public void reload() {
        SwingUtilities.invokeLater(this::loadData);
    }

    /* ================= CARD ================= */
    private JPanel createCard(String title, String icon, JLabel value, Color color) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(320, 80));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(50, 50));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(color);
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JPanel textPanel = new JPanel(new GridLayout(2,1));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(120,120,140));

        value.setFont(new Font("Segoe UI", Font.BOLD, 18));
        value.setForeground(new Color(40,40,80));

        textPanel.add(lblTitle);
        textPanel.add(value);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245,247,255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JLabel createValueLabel() {
        return new JLabel("0");
    }

    /* ================= CHART ================= */
    private JPanel createRevenueChart() {

        // Khởi tạo dataset rỗng
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "", // bỏ title mặc định
                "Tháng",
                "VNĐ",
                dataset
        );

        /* ===== STYLE ===== */
        chart.setBackgroundPaint(new Color(245,247,255));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220,220,220));

        /* ===== BAR COLOR ===== */
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52,152,219)); // xanh đẹp
        renderer.setMaximumBarWidth(0.08); // thanh mỏng đẹp hơn

        /* ===== BO GÓC BAR ===== */
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        /* ===== HIỆN SỐ TRÊN CỘT ===== */
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));

        /* ===== AXIS ===== */
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        plot.getDomainAxis().setAxisLineVisible(false);
        plot.getRangeAxis().setAxisLineVisible(false);

        /* ===== TITLE CUSTOM ===== */
        JLabel title = new JLabel("📈 Doanh thu các tháng trong năm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

        chartPanel = new ChartPanel(chart); // Gán vào biến toàn cục
        chartPanel.setPreferredSize(new Dimension(200, 260));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }
}