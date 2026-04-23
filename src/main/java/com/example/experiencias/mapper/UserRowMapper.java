package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.User;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User map(ResultSet rs) throws SQLException {
    	return new User(
    			rs.getInt("id"),
    			rs.getString("nombre"),
    			rs.getString("email"),
    			rs.getString("password"),
    			rs.getString("rol"),
    			rs.getTimestamp("fecha_registro").toLocalDateTime(),
    			rs.getBoolean("deleted")
    			);
    }
}