package org.example.controller;

import org.example.entity.Voucher;
import org.example.service.VoucherService;
import org.example.service.impl.VoucherServiceImpl;

import java.util.List;

public class VoucherController {
    private final VoucherService service = new VoucherServiceImpl();

    public List<Voucher> getAll(String keyword, String status) {
        return service.search(keyword, status);
    }

    public Voucher getByCode(String code) {
        return service.findByCode(code);
    }

    public void add(Voucher v) {
        service.add(v);
    }

    public void update(Voucher v) {
        service.update(v);
    }

    public void delete(int id) {
        service.delete(id);
    }

    public void useVoucher(Voucher v) {
        service.useVoucher(v);
    }
}