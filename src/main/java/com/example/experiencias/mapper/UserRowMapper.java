package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.User;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"), // ⚠️ hash
            rs.getString("role"),
            rs.getTimestamp("fecha_creacion").toLocalDateTime()
        );
    }
}