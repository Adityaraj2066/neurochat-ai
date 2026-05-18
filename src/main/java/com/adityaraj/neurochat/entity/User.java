package com.adityaraj.neurochat.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // ✅ ADD THIS LINE (IMPORTANT)
    private String username;
    private String password;

    // 🔥 ROLE SYSTEM
    private String role = "USER"; // USER / PRO

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER";
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}