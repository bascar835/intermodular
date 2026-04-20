package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.dto.UserResponse;

public class UserResponseRowMapper implements RowMapper<UserResponse> {

    @Override
    public UserResponse map(ResultSet rs) throws SQLException {
        return new UserResponse(
            rs.getInt("id"),
            rs.getString("nombre"),   
            rs.getString("email"),
            rs.getString("rol")       
        );
    }
}