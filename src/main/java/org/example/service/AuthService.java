package org.example.service;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;

public interface AuthService {

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    Account login(LoginRequest request);

    /**
     * Đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    boolean register(RegisterRequest request);

    boolean existsByUsername(String username);
}
