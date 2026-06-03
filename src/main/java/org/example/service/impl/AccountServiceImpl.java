package org.example.service.impl;

import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AccountService;


import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }


    /**
     * Lấy tất cả tài khoản.
     *
     * @return danh sách tài khoản
     */
    @Override
    public List<Account> findAll() {
        System.out.println("Lấy danh sách tài khoản");
        return accountRepository.findAll();
    }

    /**
     * Tìm tài khoản theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findByUsername(String username) {
        System.out.println("Tìm tài khoản với username" + username);
        return  accountRepository.findByUsername(username);
    }




    /**
     * Tạo tài khoản mới.
     * @param account tài khoản cần cập nhật
     */
    @Override
    public void create(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account không được null");
        }

        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username không được để trống");
        }

        if (existsByUsername(account.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        accountRepository.save(account);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }




    /**
     * Cập nhật tài khoản.
     *
     * @param account tài khoản cần cập nhật
     */
    @Override
    public void update(Account account) {

        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new RuntimeException("ID không hợp lệ");
        }

        if (account.getPassword() == null || account.getPassword().isBlank()) {
            throw new RuntimeException("Password không được trống");
        }

        accountRepository.update(account);
    }

    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(int id) {
        System.out.println("Xóa tài khoản" + id);
        accountRepository.deleteById(id);
    }

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    @Override
    public boolean existsByUsername(String username) {
        System.out.println("Check exists username"+ username);
        return accountRepository.existsByUsername(username);
    }




}
