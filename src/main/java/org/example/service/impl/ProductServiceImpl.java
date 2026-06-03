package org.example.service.impl;

import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl() {
        this.productRepository = new ProductRepositoryImpl();
    }

    /**
     * Lấy danh sách tất cả sản phẩm
     */
    @Override
    public List<Product> findAll() {
        System.out.println("Lấy danh sách sản phẩm");
        return productRepository.findAll();
    }

    /**
     * Tìm sản phẩm theo ID
     */
    @Override
    public Product findById(int id) {
        System.out.println("Tìm sản phẩm theo ID: " + id);
        return productRepository.findById(id);
    }

    /**
     * Tìm sản phẩm theo tên
     */
    @Override
    public List<Product> findByName(String name) {
        System.out.println("Tìm sản phẩm theo tên: " + name);
        return productRepository.findByName(name);
    }

    /**
     * Tạo sản phẩm mới
     */
    @Override
    public void create(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Product không được null");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải > 0");
        }

        productRepository.save(product);
    }

    /**
     * Cập nhật sản phẩm
     */
    @Override
    public void update(Product product) {
        System.out.println("Cập nhật sản phẩm: " + product.getName());

        if (product.getId() <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }

        productRepository.update(product);
    }

    /**
     * Xóa sản phẩm theo ID
     */
    @Override
    public void deleteById(int id) {
        System.out.println("Xóa sản phẩm ID = " + id);
        productRepository.deleteById(id);
    }
}
