package org.example.service.impl;

import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;
import org.example.repository.impl.VoucherRepositoryImpl;
import org.example.service.VoucherService;

import java.time.LocalDate;
import java.util.List;

public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository repo = new VoucherRepositoryImpl();

    @Override
    public List<Voucher> search(String keyword, String status) {
        List<Voucher> list = repo.findAll(keyword, status);
        LocalDate today = LocalDate.now();
        
        // Kiểm tra tự động hết hạn khi tải dữ liệu
        for (Voucher v : list) {
            if (today.isAfter(v.getEndDate()) && !"Hết hạn".equals(v.getStatus())) {
                v.setStatus("Hết hạn");
                repo.update(v);
            }
        }
        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        return repo.findByCode(code);
    }

    @Override
    public void add(Voucher voucher) {
        // Kiểm tra xem mã đã tồn tại chưa
        if (repo.findByCode(voucher.getCode()) != null) {
            throw new RuntimeException("Mã Voucher này đã tồn tại trên hệ thống!");
        }
        repo.save(voucher);
    }

    @Override
    public void update(Voucher voucher) {
        repo.update(voucher);
    }

    @Override
    public void delete(int id) {
        repo.delete(id);
    }

    @Override
    public void useVoucher(Voucher voucher) {
        if (voucher == null) return;
        
        int currentUsed = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;
        int limit = voucher.getUsageLimit() != null ? voucher.getUsageLimit() : 0;
        
        voucher.setUsedCount(currentUsed + 1);
        
        if (limit > 0 && voucher.getUsedCount() >= limit) {
            voucher.setStatus("Đã sử dụng");
        }
        
        repo.update(voucher);
    }
}