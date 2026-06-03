package org.example.repository;

import org.example.entity.Product;
import java.util.List;

public interface ProductRepository {

    List<Product> findAll();

    Product findById(int id);

    List<Product> findByName(String name);

    void save(Product product);

    void update(Product product);

    void deleteById(int id);
}
