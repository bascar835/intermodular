package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.User;

public class UserMapper implements RowMapper<User> {

    @Override
    public User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("nombre"),      // era "name"
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("rol"),         // era "role"
            rs.getTimestamp("fecha_registro").toLocalDateTime(),  // era "fecha_creacion"
            rs.getBoolean("deleted")
        );
    }
}