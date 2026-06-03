package org.example.service;

import org.example.entity.Account;

import java.util.List;

public interface AccountService {

    /**
     * Lấy tất cả tài khoản.
     * @return danh sách tài khoản
     */
    List<Account> findAll();

    /**
     * Tìm tài khoản theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    Account findByUsername(String username);

    /**
     * Tạo tài khoản mới.
     * @param account tài khoản cần cập nhật
     */
    void create(Account account);


    Account findById(Long id);

    /**
     * Cập nhật tài khoản.
     * @param account tài khoản cần cập nhật
     */
    void update(Account account);

    /**
     * xóa tài khoản.
     * @param id tài khoản cần cập nhật
     */
    void deleteById(int id);

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByUsername(String username);





}

