package org.example.controller;

import org.example.entity.Inventory;
import org.example.service.InventoryService;
import org.example.service.impl.InventoryServiceImpl;

import java.util.List;

public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController() {
        this.inventoryService = new InventoryServiceImpl();
    }

    /* ================== LOAD TỒN KHO ================== */
    public List<Inventory> getAllInventory() {
        return inventoryService.findAll();
    }

    /* ================== NHẬP KHO ================== */
    public void importStock(int productId, int quantity, String note) {
        inventoryService.importInventory(productId, quantity, note);
    }

    /* ================== XUẤT KHO ================== */
    public void exportStock(int productId, int quantity, String note) {
        inventoryService.exportInventory(productId, quantity, note);
    }

    /* ================== LỊCH SỬ ================== */
    public List<Inventory> getHistoryByProduct(int productId) {
        return inventoryService.findHistoryByProduct(productId);
    }

    public void deleteHistory(int productId) {
        inventoryService.deleteHistoryByProduct(productId);
    }
}
