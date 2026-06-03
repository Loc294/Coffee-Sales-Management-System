package org.example.repository;

import org.example.entity.Voucher;
import java.util.List;

public interface VoucherRepository {
    List<Voucher> findAll(String keyword, String status);
    Voucher findByCode(String code);
    void save(Voucher voucher);
    void update(Voucher voucher);
    void delete(int id);
}