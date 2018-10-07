package com.demo.oauth2.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_user")
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "username", length = 150)
    private String username;
    @Column(name = "password", length = 100)
    private String password;

    public Account() {
    }

    public Account(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
