package org.example.view;

import org.example.controller.ProductController;
import org.example.controller.RecipeController;
import org.example.entity.Product;
import org.example.entity.Recipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecipeManagementPanel extends JPanel {

    private JComboBox<Product> cboProduct;
    private JTable table;
    private DefaultTableModel tableModel;

    private final ProductController productController = new ProductController();
    private final RecipeController recipeController = new RecipeController();

    public RecipeManagementPanel() {

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        /* ================= HEADER ================= */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("QUẢN LÝ CÔNG THỨC PHA CHẾ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        /* ================= TOP PANEL ================= */
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(Color.WHITE);

        JLabel lblProduct = new JLabel("Sản phẩm:");
        lblProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboProduct = new JComboBox<>();
        cboProduct.setPreferredSize(new Dimension(250, 35));

        JButton btnLoad = createButton("Xem", new Color(0, 123, 255));

        top.add(lblProduct);
        top.add(cboProduct);
        top.add(btnLoad);

        add(top, BorderLayout.BEFORE_FIRST_LINE);

        /* ================= TABLE ================= */
        String[] cols = {"ID", "Nguyên liệu", "Số lượng", "Đơn vị"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        // Zebra row
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(scroll, BorderLayout.CENTER);

        /* ================= BUTTONS ================= */
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Thêm", new Color(40, 167, 69));
        JButton btnEdit = createButton("Sửa", new Color(255, 193, 7));
        JButton btnDelete = createButton("Xóa", new Color(220, 53, 69));
        JButton btnRefresh = createButton("Làm mới", new Color(108, 117, 125));

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        /* ================= DATA ================= */
        loadProducts();

        /* ================= EVENTS ================= */
        btnLoad.addActionListener(e -> loadRecipe());
        btnRefresh.addActionListener(e -> loadRecipe());
        btnAdd.addActionListener(e -> addRecipe());
        btnEdit.addActionListener(e -> editRecipe());
        btnDelete.addActionListener(e -> deleteRecipe());
    }

    /* ================= STYLE BUTTON ================= */
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(100, 35));
        return btn;
    }

    /* ================= LOAD PRODUCTS ================= */
    private void loadProducts() {
        cboProduct.removeAllItems();
        List<Product> products = productController.getAllProduct();
        for (Product p : products) {
            cboProduct.addItem(p);
        }
    }

    /* ================= LOAD RECIPE ================= */
    private void loadRecipe() {
        Product p = (Product) cboProduct.getSelectedItem();
        if (p == null) return;

        tableModel.setRowCount(0);

        List<Recipe> list = recipeController.getRecipeByProduct(p.getId());

        for (Recipe r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getIngredientName(),
                    r.getAmount(),
                    r.getUnit()
            });
        }
    }

    /* ================= ADD ================= */
    private void addRecipe() {
        Product p = (Product) cboProduct.getSelectedItem();
        if (p == null) return;

        JTextField txtName = new JTextField();
        JTextField txtAmount = new JTextField();
        JTextField txtUnit = new JTextField();

        Object[] form = {
                "Nguyên liệu", txtName,
                "Số lượng", txtAmount,
                "Đơn vị", txtUnit
        };

        int rs = JOptionPane.showConfirmDialog(this, form,
                "Thêm nguyên liệu", JOptionPane.OK_CANCEL_OPTION);

        if (rs == JOptionPane.OK_OPTION) {
            recipeController.createRecipe(
                    p.getId(),
                    txtName.getText(),
                    Double.parseDouble(txtAmount.getText()),
                    txtUnit.getText()
            );
            loadRecipe();
        }
    }

    /* ================= EDIT ================= */
    private void editRecipe() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        double amount = (double) tableModel.getValueAt(row, 2);
        String unit = (String) tableModel.getValueAt(row, 3);

        JTextField txtName = new JTextField(name);
        JTextField txtAmount = new JTextField(String.valueOf(amount));
        JTextField txtUnit = new JTextField(unit);

        Object[] form = {
                "Nguyên liệu", txtName,
                "Số lượng", txtAmount,
                "Đơn vị", txtUnit
        };

        int rs = JOptionPane.showConfirmDialog(this, form,
                "Sửa nguyên liệu", JOptionPane.OK_CANCEL_OPTION);

        if (rs == JOptionPane.OK_OPTION) {
            Recipe r = new Recipe();
            r.setId(id);
            r.setIngredientName(txtName.getText());
            r.setAmount(Double.parseDouble(txtAmount.getText()));
            r.setUnit(txtUnit.getText());

            recipeController.updateRecipe(r);
            loadRecipe();
        }
    }

    /* ================= DELETE ================= */
    private void deleteRecipe() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa nguyên liệu này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            recipeController.deleteRecipe(id);
            loadRecipe();
        }
    }
}