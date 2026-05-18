package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class CheckoutPreview {
    private Integer id;
    private int userId;
    private String data;
    private LocalDateTime createdAt;

    public CheckoutPreview(Integer id, int userId, String data, LocalDateTime createdAt) {
        this.id = id; this.userId = userId; this.data = data; this.createdAt = createdAt;
    }

    public Integer getId()                      { return id; }
    public void setId(Integer id)               { this.id = id; }
    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }
    public String getData()                     { return data; }
    public void setData(String data)            { this.data = data; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }

    @Override
    public String toString() {
        return "CheckoutPreview[id=" + id + ", userId=" + userId + ", createdAt=" + createdAt + "]";
    }
}
