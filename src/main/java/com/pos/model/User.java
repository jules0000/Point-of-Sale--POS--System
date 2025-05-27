package com.pos.model;

import java.time.LocalDateTime;

/**
 * Represents a user in the POS system
 */
public class User extends BaseModel {
    public enum Role {
        ADMIN("Admin"),
        CASHIER("Cashier");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Role fromString(String text) {
            for (Role role : Role.values()) {
                if (role.value.equalsIgnoreCase(text)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("No role with text " + text + " found");
        }
        
        @Override
        public String toString() {
            return value;
        }
    }

    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime lastLogin;
    
    // Default constructor
    public User() {
        super();
        this.active = true;
    }
    
    // Getters and Setters
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    public boolean isCashier() {
        return role == Role.CASHIER;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%s, username='%s', fullName='%s', role=%s, email='%s', phone='%s', lastLogin=%s}",
                getId(), username, fullName, role, email, phone, lastLogin);
    }
} 