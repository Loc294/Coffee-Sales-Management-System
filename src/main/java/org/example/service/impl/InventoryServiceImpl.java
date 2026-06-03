package org.example.service.impl;

import org.example.entity.Inventory;
import org.example.event.DataChangeEventBus;
import org.example.repository.InventoryRepository;
import org.example.repository.impl.InventoryRepositoryImpl;
import org.example.service.InventoryService;

import java.util.List;

public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repo = new InventoryRepositoryImpl();

    @Override
    public List<Inventory> findAll() {
        return repo.findAll();
    }

    @Override
    public void importInventory(int productId, int quantity, String note) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải > 0");
        }
        repo.importInventory(productId, quantity, note);

        DataChangeEventBus.notifyChange();
    }

    @Override
    public void exportInventory(int productId, int quantity, String note) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải > 0");
        }
        repo.exportInventory(productId, quantity, note);

        DataChangeEventBus.notifyChange();
    }

    @Override
    public List<Inventory> findHistoryByProduct(int productId) {
        return repo.findHistoryByProduct(productId);
    }

    @Override
    public void deleteHistoryByProduct(int productId) {
        // Bạn có thể thêm kiểm tra quyền hạn ở đây nếu cần
        repo.deleteHistoryByProduct(productId);
    }
}
