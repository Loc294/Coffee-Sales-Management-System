package org.example.controller;

import org.example.entity.Product;
import org.example.service.ProductService;
import org.example.service.impl.ProductServiceImpl;

import java.util.List;

public class ProductController {

    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductServiceImpl();
    }

    /**
     * Lấy danh sách tất cả product
     */
    public List<Product> getAllProduct() {
        return productService.findAll();
    }

    /**
     * Tạo mới product
     */
    public void createProduct(String name, double price, boolean is_active) {
        Product product = new Product(name, price, is_active);
        productService.create(product);
    }

    /**
     * Cập nhật product
     */
    public void updateProduct(Product product) {
        productService.update(product);
    }

    /**
     * Xóa product theo ID
     */
    public void deleteProduct(Product product) {
        productService.deleteById(product.getId());
    }

    /**
     * Tìm product theo tên (nếu cần)
     */
    public List<Product> findProductByName(String name) {
        return productService.findByName(name);
    }
    public Product findProductById(int id) {
        return productService.findById(id);
    }
}
