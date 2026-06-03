package org.example.view;

import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.controller.VoucherController;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.Product;
import org.example.entity.Voucher;
import org.example.session.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductManagementPanel extends JPanel {

    // ── Palette (Giống AccountManagementPanel) ───────────────────────────────
    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_GREEN  = new Color(0x22C55E);
    private static final Color BTN_AMBER  = new Color(0xF59E0B);
    private static final Color BTN_RED    = new Color(0xEF4444);
    private static final Color BTN_SLATE  = new Color(0x64748B);
    private static final Color BTN_BLUE   = new Color(0x3B82F6);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    private JTable productTable;
    private DefaultTableModel productTableModel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotal;

    private final ProductController productController = new ProductController();
    private final OrderController   orderController   = new OrderController();
    private final VoucherController voucherController = new VoucherController();

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public ProductManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        initUI();
        loadDataFromDB();
    }

    private void initUI() {
        boolean isAdmin = UserSession.getInstance().isAdmin();

        /* ===== HEADER (BANNER) ===== */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("☕");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Sản Phẩm");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Thêm mới, cập nhật sản phẩm và quản lý giỏ hàng thanh toán");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(titleLabel);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        /* ===== CONTROL BAR (SEARCH + BUTTONS) ===== */
        JPanel controlBar = new JPanel(new BorderLayout(12, 0));
        controlBar.setOpaque(false);
        controlBar.setBorder(new EmptyBorder(16, 20, 12, 20));

        // Search Group
        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchGroup.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 36));
        txtSearch.setFont(FONT_BODY);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên sản phẩm...");

        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đang bán", "Ngừng bán"});
        cbStatus.setPreferredSize(new Dimension(130, 36));
        cbStatus.setFont(FONT_BODY);

        JButton btnSearch = createButton("🔍 Tìm", BTN_BLUE);
        
        searchGroup.add(txtSearch);
        searchGroup.add(cbStatus);
        searchGroup.add(btnSearch);

        // Action Group
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        JButton btnAdd     = createButton("＋ Thêm",       BTN_GREEN);
        JButton btnEdit    = createButton("✎ Sửa",        BTN_AMBER);
        JButton btnDelete  = createButton("✕ Xóa",        BTN_RED);
        JButton btnRefresh = createButton("↻ Làm mới",    BTN_SLATE);
        JButton btnAddCart = createButton("🛒 + Giỏ hàng", BTN_BLUE);

        btnAdd.setVisible(isAdmin);
        btnEdit.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);

        actionGroup.add(btnAdd);
        actionGroup.add(btnEdit);
        actionGroup.add(btnDelete);
        actionGroup.add(btnRefresh);
        actionGroup.add(btnAddCart);

        controlBar.add(searchGroup, BorderLayout.WEST);
        controlBar.add(actionGroup, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setOpaque(false);
        northContainer.add(header, BorderLayout.NORTH);
        northContainer.add(controlBar, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);

        /* ===== CENTER CONTENT (SPLIT PANE) ===== */
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // PRODUCT TABLE
        productTableModel = new DefaultTableModel(
                new String[]{"ID", "Tên sản phẩm", "Giá bán", "Trạng thái", "Ngày tạo", "Cập nhật"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(32);
        productTable.setFont(FONT_BODY);
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        productScroll.getViewport().setBackground(Color.WHITE);

        // CART TABLE
        cartTableModel = new DefaultTableModel(
                new String[]{"Tên sản phẩm", "Đơn giá", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(FONT_BODY);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Giỏ hàng hiện tại"));
        cartScroll.getViewport().setBackground(Color.WHITE);

        // Cart Bottom
        lblTotal = new JLabel("Tổng tiền: 0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(BTN_RED);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRemoveCart = createFullWidthButton("Xóa dòng đã chọn", BTN_RED);
        JButton btnClearCart  = createFullWidthButton("Xóa tất cả",       BTN_SLATE);
        JButton btnCheckout   = createFullWidthButton("🚀  TIẾN HÀNH THANH TOÁN", BTN_GREEN);

        JPanel cartBottomPanel = new JPanel();
        cartBottomPanel.setLayout(new BoxLayout(cartBottomPanel, BoxLayout.Y_AXIS));
        cartBottomPanel.setOpaque(false);
        cartBottomPanel.setBorder(new EmptyBorder(12, 6, 6, 6));
        cartBottomPanel.add(lblTotal);
        cartBottomPanel.add(Box.createVerticalStrut(12));
        cartBottomPanel.add(btnRemoveCart);
        cartBottomPanel.add(Box.createVerticalStrut(6));
        cartBottomPanel.add(btnClearCart);
        cartBottomPanel.add(Box.createVerticalStrut(6));
        cartBottomPanel.add(btnCheckout);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setOpaque(false);
        cartPanel.add(cartScroll,      BorderLayout.CENTER);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productScroll, cartPanel);
        splitPane.setDividerLocation(750);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        centerPanel.add(splitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        btnRefresh.addActionListener(e -> loadDataFromDB());
        btnSearch.addActionListener(e -> loadDataFromDB());
        cbStatus.addActionListener(e -> loadDataFromDB());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnAddCart.addActionListener(e -> addToCart());
        productTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addToCart();
            }
        });
        btnRemoveCart.addActionListener(e -> removeFromCart());
        btnClearCart.addActionListener(e -> { cartTableModel.setRowCount(0); updateTotal(); });
        btnCheckout.addActionListener(e -> openCheckoutDialog());

        cartTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 2) {
                int row = e.getFirstRow();
                try {
                    int qty = Integer.parseInt(cartTableModel.getValueAt(row, 2).toString());
                    double unitPrice = (double) cartTableModel.getValueAt(row, 1);
                    if (qty <= 0) cartTableModel.removeRow(row);
                    else cartTableModel.setValueAt(unitPrice * qty, row, 3);
                    updateTotal();
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        if (!"Đang bán".equals(productTableModel.getValueAt(row, 3))) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã ngừng bán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return;
        }
        String name  = (String) productTableModel.getValueAt(row, 1);
        double price = (double) productTableModel.getValueAt(row, 2);
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (cartTableModel.getValueAt(i, 0).equals(name)) {
                int qty = (int) cartTableModel.getValueAt(i, 2);
                cartTableModel.setValueAt(qty + 1, i, 2);
                cartTableModel.setValueAt(price * (qty + 1), i, 3);
                updateTotal(); return;
            }
        }
        cartTableModel.addRow(new Object[]{name, price, 1, price});
        updateTotal();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        cartTableModel.removeRow(row); updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) total += (double) cartTableModel.getValueAt(i, 3);
        lblTotal.setText(String.format("Tổng tiền: %,.0f đ", total));
    }

    private void openCheckoutDialog() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thanh Toán", true);
        dialog.setSize(600, 680); // Giảm chiều cao
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel summaryModel = new DefaultTableModel(
                new String[]{"Sản phẩm", "Đơn giá", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        double subtotal = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            summaryModel.addRow(new Object[]{
                    cartTableModel.getValueAt(i, 0), cartTableModel.getValueAt(i, 1),
                    cartTableModel.getValueAt(i, 2), cartTableModel.getValueAt(i, 3)
            });
            subtotal += (double) cartTableModel.getValueAt(i, 3);
        }
        final double[] subtotalRef = {subtotal};

        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(26);
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        summaryScroll.setPreferredSize(new Dimension(560, 130));
        summaryScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng"));

        JComboBox<String> cbPayment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        JTextField txtNote = new JTextField();

        JPanel orderInfoPanel = new JPanel(new GridLayout(2, 2, 10, 8)); // Giảm số hàng
        orderInfoPanel.setOpaque(false);
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
        orderInfoPanel.add(new JLabel("  Phương thức TT:")); orderInfoPanel.add(cbPayment);
        orderInfoPanel.add(new JLabel("  Ghi chú:"));        orderInfoPanel.add(txtNote);

        JComboBox<String> cbBank      = new JComboBox<>(new String[]{"Vietcombank","Techcombank","BIDV","Agribank","MB Bank","VPBank","ACB","Sacombank","TPBank"});
        JTextField txtAccountNumber   = new JTextField();
        JTextField txtAccountName     = new JTextField();
        JTextField txtTransferContent = new JTextField("Thanh toan don hang");

        JPanel bankPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        bankPanel.setOpaque(false);
        bankPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BTN_BLUE, 1),
                "Thông tin chuyển khoản", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), BTN_BLUE));
        bankPanel.add(new JLabel("  Ngân hàng:"));     bankPanel.add(cbBank);
        bankPanel.add(new JLabel("  Số tài khoản:"));  bankPanel.add(txtAccountNumber);
        bankPanel.add(new JLabel("  Tên tài khoản:")); bankPanel.add(txtAccountName);
        bankPanel.add(new JLabel("  Nội dung CK:"));   bankPanel.add(txtTransferContent);
        bankPanel.setVisible(false);

        cbPayment.addActionListener(e -> {
            bankPanel.setVisible("Chuyển khoản".equals(cbPayment.getSelectedItem()));
            dialog.revalidate(); dialog.repaint();
        });

        JTextField txtVoucherCode = new JTextField();
        txtVoucherCode.setPreferredSize(new Dimension(180, 30));
        JButton btnApplyVoucher   = createButton("Áp dụng", BTN_BLUE);
        btnApplyVoucher.setPreferredSize(new Dimension(100, 30));
        JLabel lblVoucherInfo     = new JLabel("Chưa áp dụng voucher");
        lblVoucherInfo.setForeground(Color.GRAY);
        lblVoucherInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel voucherInputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        voucherInputRow.setOpaque(false);
        voucherInputRow.add(new JLabel("  Mã voucher:"));
        voucherInputRow.add(txtVoucherCode);
        voucherInputRow.add(btnApplyVoucher);

        JPanel voucherPanel = new JPanel(new BorderLayout(0, 4));
        voucherPanel.setOpaque(false);
        voucherPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BTN_GREEN, 1),
                "Voucher giảm giá", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), BTN_GREEN));
        voucherPanel.add(voucherInputRow, BorderLayout.NORTH);
        voucherPanel.add(lblVoucherInfo,  BorderLayout.CENTER);

        JLabel lblSubtotal    = new JLabel(String.format("  Tạm tính:       %,.0f đ", subtotal));
        JLabel lblDiscount    = new JLabel("  Giảm giá:       0 đ");
        JLabel lblFinalTotal  = new JLabel(String.format("  Tổng thanh toán: %,.0f đ", subtotal));

        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDiscount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDiscount.setForeground(BTN_GREEN);
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFinalTotal.setForeground(BTN_RED);

        JPanel totalPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        totalPanel.setOpaque(false);
        totalPanel.setBorder(new EmptyBorder(6, 4, 6, 4));
        totalPanel.add(lblSubtotal);
        totalPanel.add(lblDiscount);
        totalPanel.add(lblFinalTotal);

        final Voucher[] appliedVoucher = {null};
        final double[]  discountAmount = {0};
        final double[]  finalTotal     = {subtotal};

        btnApplyVoucher.addActionListener(e -> {
            String code = txtVoucherCode.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã voucher!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Voucher v = voucherController.getByCode(code);
                if (v == null) {
                    lblVoucherInfo.setText("❌ Mã voucher không tồn tại!");
                    lblVoucherInfo.setForeground(BTN_RED);
                    appliedVoucher[0] = null; discountAmount[0] = 0; finalTotal[0] = subtotalRef[0];
                } else if (!"Còn hiệu lực".equals(v.getStatus())) {
                    lblVoucherInfo.setText("❌ Voucher không khả dụng!");
                    lblVoucherInfo.setForeground(BTN_RED);
                    appliedVoucher[0] = null; discountAmount[0] = 0; finalTotal[0] = subtotalRef[0];
                } else {
                    if ("PERCENT".equals(v.getDiscountType()) || "Phần trăm".equals(v.getDiscountType())) {
                        discountAmount[0] = subtotalRef[0] * v.getDiscountValue() / 100.0;
                        lblVoucherInfo.setText(String.format("✅ Giảm %.0f%% → -  %,.0f đ", v.getDiscountValue(), discountAmount[0]));
                    } else {
                        discountAmount[0] = Math.min(v.getDiscountValue(), subtotalRef[0]);
                        lblVoucherInfo.setText(String.format("✅ Giảm trực tiếp: - %,.0f đ", discountAmount[0]));
                    }
                    finalTotal[0] = Math.max(0, subtotalRef[0] - discountAmount[0]);
                    appliedVoucher[0] = v;
                    lblVoucherInfo.setForeground(BTN_GREEN);
                }
                lblDiscount.setText(String.format("  Giảm giá:       - %,.0f đ", discountAmount[0]));
                lblFinalTotal.setText(String.format("  Tổng thanh toán: %,.0f đ", finalTotal[0]));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnConfirm = createButton("✅ Xác nhận thanh toán", BTN_GREEN);
        JButton btnCancel  = createButton("Hủy", BTN_RED);
        btnConfirm.setPreferredSize(new Dimension(200, 38));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(btnConfirm); btnPanel.add(btnCancel);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 14, 4, 14));
        content.add(summaryScroll);
        content.add(Box.createVerticalStrut(8));
        content.add(orderInfoPanel);
        content.add(Box.createVerticalStrut(6));
        content.add(bankPanel);
        content.add(Box.createVerticalStrut(6));
        content.add(voucherPanel);
        content.add(Box.createVerticalStrut(6));
        content.add(totalPanel);

        JScrollPane dialogScroll = new JScrollPane(content);
        dialogScroll.setBorder(null);
        dialog.add(dialogScroll, BorderLayout.CENTER);
        dialog.add(btnPanel,     BorderLayout.SOUTH);

        btnConfirm.addActionListener(e -> {
            try {
                if ("Chuyển khoản".equals(cbPayment.getSelectedItem())) {
                    if (txtAccountNumber.getText().trim().isEmpty() || txtAccountName.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin chuyển khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
                    }
                }
                String paymentMethod = (String) cbPayment.getSelectedItem();

                StringBuilder fullNote = new StringBuilder();
                fullNote.append("TT: ").append(paymentMethod);
                if (appliedVoucher[0] != null) fullNote.append(" | Voucher: ").append(appliedVoucher[0].getCode());
                String note = txtNote.getText().trim();
                if (!note.isEmpty()) fullNote.append(" | ").append(note);

                List<OrderDetail> details = new ArrayList<>();
                List<Product> products = productController.getAllProduct();
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String pName = (String) cartTableModel.getValueAt(i, 0);
                    double pPrice = (double) cartTableModel.getValueAt(i, 1);
                    int pQty = (int) cartTableModel.getValueAt(i, 2);
                    int pId = products.stream().filter(p -> p.getName().equals(pName)).mapToInt(Product::getId).findFirst().orElse(0);
                    details.add(new OrderDetail(pId, pName, pPrice, pQty));
                }

                // Bỏ qua việc hoàn thành đơn hàng ngay lập tức
                Order order = orderController.createOrder(details, fullNote.toString());
                // orderController.completeOrder(order.getId()); // Dòng này đã được xóa

                // Cập nhật voucher sau khi thanh toán thành công
                if (appliedVoucher[0] != null) {
                    voucherController.useVoucher(appliedVoucher[0]);
                }

                JOptionPane.showMessageDialog(dialog, "✅ Tạo đơn hàng thành công!\nVui lòng chuyển sang tab Quản lý đơn hàng để xử lý.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                cartTableModel.setRowCount(0); updateTotal(); dialog.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void loadDataFromDB() {
        productTableModel.setRowCount(0);
        String keyword      = txtSearch.getText().trim().toLowerCase();
        String statusFilter = Objects.requireNonNull(cbStatus.getSelectedItem()).toString();
        for (Product p : productController.getAllProduct()) {
            if (!keyword.isEmpty() && !p.getName().toLowerCase().contains(keyword)) continue;
            String statusText = p.isActive() ? "Đang bán" : "Ngừng bán";
            if (!statusFilter.equals("Tất cả") && !statusFilter.equals(statusText)) continue;
            productTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), statusText, p.getCreatedTime(), p.getUpdatedTime()});
        }
    }

    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm mới", true);
        dialog.setSize(400, 240); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        JTextField txtName = new JTextField(), txtPrice = new JTextField();
        JCheckBox chkActive = new JCheckBox("Đang bán", true);
        form.add(new JLabel("Tên sản phẩm:")); form.add(txtName);
        form.add(new JLabel("Giá bán:"));      form.add(txtPrice);
        form.add(new JLabel("Trạng thái:"));   form.add(chkActive);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Lưu", BTN_GREEN);
        JButton btnCan  = createButton("Hủy", BTN_RED);
        btnP.add(btnSave); btnP.add(btnCan);
        dialog.add(form, BorderLayout.CENTER); dialog.add(btnP, BorderLayout.SOUTH);
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (name.isEmpty() || price <= 0) { JOptionPane.showMessageDialog(dialog, "Vui lòng nhập hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                productController.createProduct(name, price, chkActive.isSelected());
                dialog.dispose(); loadDataFromDB();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Lỗi nhập liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        });
        btnCan.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void openEditDialog() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;
        int pid = (int) productTableModel.getValueAt(row, 0);
        String name = (String) productTableModel.getValueAt(row, 1);
        double price = (double) productTableModel.getValueAt(row, 2);
        boolean active = "Đang bán".equals(productTableModel.getValueAt(row, 3));
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa sản phẩm", true);
        dialog.setSize(400, 240); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        JTextField txtName = new JTextField(name), txtPrice = new JTextField(String.valueOf(price));
        JCheckBox chkActive = new JCheckBox("Đang bán", active);
        form.add(new JLabel("Tên sản phẩm:")); form.add(txtName);
        form.add(new JLabel("Giá bán:"));      form.add(txtPrice);
        form.add(new JLabel("Trạng thái:"));   form.add(chkActive);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Cập nhật", BTN_AMBER);
        JButton btnCan  = createButton("Hủy", BTN_RED);
        btnP.add(btnSave); btnP.add(btnCan);
        dialog.add(form, BorderLayout.CENTER); dialog.add(btnP, BorderLayout.SOUTH);
        btnSave.addActionListener(e -> {
            try {
                Product p = new Product(); p.setId(pid); p.setName(txtName.getText().trim()); p.setPrice(Double.parseDouble(txtPrice.getText().trim())); p.setActive(chkActive.isSelected());
                productController.updateProduct(p); dialog.dispose(); loadDataFromDB();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        });
        btnCan.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;
        int pid = (int) productTableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Product p = new Product(); p.setId(pid); productController.deleteProduct(p); loadDataFromDB();
        }
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    private JButton createFullWidthButton(String text, Color base) {
        JButton btn = createButton(text, base);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}