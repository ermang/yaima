package com.eg.yaima.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class AppUser extends BaseEntity{

    @Column(nullable = false, unique = true, length = 25)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
