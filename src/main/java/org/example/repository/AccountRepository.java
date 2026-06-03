package org.example.repository;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    /**
     * Lấy tất cả tài khoản
     */
    List<Account> findAll();

    /**
     * Tìm tài khoản theo username
     */
    Account findByUsername(String username);

    /**
     * Tìm tài khoản theo id
     */
    Optional<Account> findById(Long id);

    /**
     * Thêm tài khoản mới
     */
    void save(Account account);

    /**
     * Cập nhật tài khoản
     */
    void update(Account account);

    /**
     * Xóa tài khoản theo id
     */
    void deleteById(int id);

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean existsByUsername(String username);

}
