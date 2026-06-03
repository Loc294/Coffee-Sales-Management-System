package org.example.dto;

public class RegisterRequest {

    private final String username;
    private final String password;
    private final String confirmPassword;
    private final String role;

    public RegisterRequest(String username, String password,
                           String confirmPassword, String role) {
        this.username        = username;
        this.password        = password;
        this.confirmPassword = confirmPassword;
        this.role            = role;
    }

    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getRole()            { return role; }

    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}