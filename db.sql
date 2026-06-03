CREATE DATABASE IF NOT EXISTS coffee;
USE coffee;

CREATE TABLE IF NOT EXISTS account (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    role VARCHAR(20),
    is_active TINYINT(1) NOT NULL DEFAULT 1
    );

-- Thêm admin nếu chưa tồn tại
INSERT INTO account (username, password, is_active, role)
SELECT 'admin', '123456789', 1, "ADMIN"
    WHERE NOT EXISTS (
    SELECT 1 FROM account WHERE username = 'admin'
);

select * from account;


CREATE TABLE IF NOT EXISTS employee (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    position VARCHAR(50),

    account_id INT UNIQUE,  -- 1 account ↔ 1 employee

    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updateTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_account
    FOREIGN KEY (account_id)
    REFERENCES account(id)
                                           ON DELETE SET NULL
                                           ON UPDATE CASCADE
    );
INSERT INTO employee (name, phone, position, account_id)
VALUES
    ('Nguyễn Văn A', '0901234567', 'Admin', 1),
    ('Trần Thị B', '0912345678', 'Staff', NULL);
select *from employee;
SELECT id, name, account_id FROM employee;
UPDATE employee SET account_id = 2 WHERE id = 1;
UPDATE employee SET account_id = 1 WHERE id = 2;

SELECT
    e.name,
    a.username
FROM employee e
         LEFT JOIN account a ON e.account_id = a.id;

CREATE TABLE IF NOT EXISTS product (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP
    );
select * from product where id = 1;

CREATE TABLE inventory (
                           id INT AUTO_INCREMENT PRIMARY KEY,

                           product_id INT NOT NULL UNIQUE,
                           quantity INT NOT NULL DEFAULT 0,

                           createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updatedTime TIMESTAMP NULL DEFAULT NULL
        ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (product_id)
                                   REFERENCES product(id)
                                   ON DELETE CASCADE
);
select * from inventory;

CREATE TABLE inventory_history (
                                   id INT AUTO_INCREMENT PRIMARY KEY,

                                   product_id INT NOT NULL,
                                   quantity_change INT NOT NULL, -- + nhập | - xuất
                                   action ENUM('IMPORT', 'EXPORT') NOT NULL,
                                   note VARCHAR(255),

                                   createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_history_product
                                       FOREIGN KEY (product_id)
                                           REFERENCES product(id)
                                           ON DELETE CASCADE
);


select * from inventory;
select* from inventory_history;

CREATE TABLE recipe (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        product_id INT NOT NULL,
                        ingredient_name VARCHAR(100) NOT NULL,
                        amount DOUBLE NOT NULL,
                        unit VARCHAR(20) NOT NULL,
                        createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updatedTime DATETIME DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,

                        FOREIGN KEY (product_id)
                            REFERENCES product(id)
                            ON DELETE CASCADE
);

select * from recipe;


CREATE TABLE reservations (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              customer_name VARCHAR(100) NOT NULL,
                              table_number INT NOT NULL,
                              time DATETIME NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              note VARCHAR(255) NULL
);

INSERT INTO product (name, price, is_active)
VALUES
    ('Black Coffee', 20000, 1),
    ('Milk Coffee', 25000, 1);

Select * from product;

CREATE TABLE shift (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       start_time TIME NOT NULL,
                       end_time TIME NOT NULL,
                       createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updateTime DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE employee_shift (
                                id INT AUTO_INCREMENT PRIMARY KEY,

                                employee_id INT NOT NULL,
                                shift_id INT NOT NULL,
                                work_date DATE NOT NULL,

                                register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT uk_employee_shift
                                    UNIQUE (employee_id, shift_id, work_date),

                                CONSTRAINT fk_es_employee
                                    FOREIGN KEY (employee_id)
                                        REFERENCES employee(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_es_shift
                                    FOREIGN KEY (shift_id)
                                        REFERENCES shift(id)
                                        ON DELETE CASCADE
);
ALTER TABLE employee
DROP FOREIGN KEY fk_employee_account;
ALTER TABLE employee
DROP COLUMN account_id;

CREATE TABLE vouchers (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          code VARCHAR(50) UNIQUE NOT NULL,          -- Mã voucher
                          discount_type ENUM('PERCENT','AMOUNT') NOT NULL, -- Loại giảm: % hoặc số tiền
                          discount_value DECIMAL(10,2) NOT NULL,     -- Giá trị giảm
                          start_date DATE NOT NULL,                  -- Ngày bắt đầu
                          end_date DATE NOT NULL,                    -- Ngày kết thúc
                          status ENUM('ACTIVE','EXPIRED','USED') NOT NULL DEFAULT 'ACTIVE', -- Trạng thái
                          usage_limit INT DEFAULT 0,                 -- Giới hạn số lượt (0 = không giới hạn)
                          used_count INT DEFAULT 0,                  -- Số lượt đã dùng
                          note VARCHAR(255)                          -- Ghi chú
);
select * from vouchers;
ALTER TABLE vouchers
    MODIFY status ENUM('Còn hiệu lực','Hết hạn','Đã sử dụng')
    NOT NULL DEFAULT 'Còn hiệu lực';
ALTER TABLE vouchers
    MODIFY discount_type ENUM('Phần trăm','Số tiền') NOT NULL;

CREATE TABLE tables (
                        table_number INT AUTO_INCREMENT PRIMARY KEY,   -- số bàn, PK
                        name VARCHAR(100) NOT NULL,                    -- tên bàn hiển thị
                        capacity INT NOT NULL DEFAULT 2,               -- sức chứa
                        status ENUM('Trống','Đang sử dụng','Đặt trước') NOT NULL DEFAULT 'Trống',
                        note VARCHAR(255) NULL
);

CREATE TABLE customer (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(20),
                          name VARCHAR(100),
                          phone VARCHAR(15),
                          email VARCHAR(100),
                          point INT,
                          status TINYINT
);

select * from customer;

INSERT INTO tables (table_number, name, capacity, status, note) VALUES
                                                                    (1, 'Bàn 1', 2, 'Trống', 'Khu ngoài trời'),
                                                                    (2, 'Bàn 2', 4, 'Đang sử dụng', 'Gần quầy bar'),
                                                                    (3, 'Bàn 3', 2, 'Đặt trước', 'Khách VIP'),
                                                                    (4, 'Bàn 4', 6, 'Trống', 'Phòng riêng'),
                                                                    (5, 'Bàn 5', 2, 'Trống', NULL),
                                                                    (6, 'Bàn 6', 4, 'Đang sử dụng', 'Có ổ cắm điện'),
                                                                    (7, 'Bàn 7', 2, 'Trống', NULL),
                                                                    (8, 'Bàn 8', 4, 'Đặt trước', 'Sinh nhật'),
                                                                    (9, 'Bàn 9', 2, 'Trống', NULL),
                                                                    (10, 'Bàn 10', 6, 'Đang sử dụng', 'Gia đình'),
                                                                    (11, 'Bàn 11', 2, 'Trống', NULL),
                                                                    (12, 'Bàn 12', 4, 'Đang sử dụng', NULL),
                                                                    (13, 'Bàn 13', 2, 'Trống', NULL),
                                                                    (14, 'Bàn 14', 4, 'Đặt trước', 'Khách công ty'),
                                                                    (15, 'Bàn 15', 2, 'Trống', NULL),
                                                                    (16, 'Bàn 16', 4, 'Đang sử dụng', NULL),
                                                                    (17, 'Bàn 17', 2, 'Trống', NULL),
                                                                    (18, 'Bàn 18', 4, 'Đang sử dụng', NULL),
                                                                    (19, 'Bàn 19', 2, 'Trống', NULL),
                                                                    (20, 'Bàn 20', 6, 'Đặt trước', 'Tiệc nhỏ');
CREATE TABLE IF NOT EXISTS customer (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        code VARCHAR(20) NOT NULL UNIQUE,   -- Mã khách hàng
    name VARCHAR(100) NOT NULL,         -- Tên khách hàng
    phone VARCHAR(15),                  -- SĐT
    email VARCHAR(100),                 -- Email
    point INT DEFAULT 0,                -- Điểm tích lũy
    status TINYINT(1) DEFAULT 1,        -- 1: hoạt động, 0: ngưng

    createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedTime TIMESTAMP NULL DEFAULT NULL
    ON UPDATE CURRENT_TIMESTAMP
    );
select * from customer;

CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        order_code VARCHAR(30) NOT NULL UNIQUE,   -- VD: ORD-20240406-001
                        total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
                        status ENUM('PENDING','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
                        note VARCHAR(255) NULL,
                        createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updatedTime TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_detail (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              order_id INT NOT NULL,
                              product_id INT NOT NULL,
                              product_name VARCHAR(100) NOT NULL,   -- snapshot tên lúc đặt
                              unit_price DECIMAL(12,2) NOT NULL,    -- snapshot giá lúc đặt
                              quantity INT NOT NULL DEFAULT 1,
                              subtotal DECIMAL(12,2) NOT NULL,      -- unit_price * quantity

                              CONSTRAINT fk_od_order
                                  FOREIGN KEY (order_id) REFERENCES orders(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_od_product
                                  FOREIGN KEY (product_id) REFERENCES product(id)
                                      ON DELETE RESTRICT
);
use coffee;
