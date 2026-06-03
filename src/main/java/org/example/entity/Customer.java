package org.example.entity;

public class Customer {

    private int id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private int point;
    private int status;

    public Customer() {}

    public Customer(int id, String code, String name,
                    String phone, String email,
                    int point, int status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.point = point;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getPoint() { return point; }
    public void setPoint(int point) { this.point = point; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
