package org.example.service;

import org.example.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findById(int id);

    List<Product> findByName(String name);

    void create(Product product);

    void update(Product product);

    void deleteById(int id);
}
