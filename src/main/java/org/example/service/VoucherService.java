package org.example.service;

import org.example.entity.Voucher;
import java.util.List;

public interface VoucherService {
    List<Voucher> search(String keyword, String status);
    Voucher findByCode(String code);
    void add(Voucher voucher);
    void update(Voucher voucher);
    void delete(int id);
    void useVoucher(Voucher voucher);
}