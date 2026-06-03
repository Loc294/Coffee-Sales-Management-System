package org.example.repository;

import org.example.entity.Inventory;
import java.util.List;

public interface InventoryRepository {

    List<Inventory> findAll();

    void importInventory(int productId, int quantity, String note);

    void exportInventory(int productId, int quantity, String note);

    void deleteHistoryByProduct(int productId);

    List<Inventory> findHistoryByProduct(int productId);
}

