package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password; // ⚠️ aquí debe ir HASH, no texto plano
    private String role;
    private LocalDateTime fechaCreacion;

    // Constructor vacío (IMPORTANTE)
    public User() {}

    // Constructor completo
    public User(Integer id, String name, String email, String password, String role, LocalDateTime fechaCreacion) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


@Override
public String toString() {
    return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", role='" + role + '\'' +
            ", fechaCreacion=" + fechaCreacion +
            '}';
}
}