package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.mapper.UserMapper;
import com.example.experiencias.mapper.UserResponseMapper;

import database.DB;

public class UserRepository extends BaseRepository<User> {

    public UserRepository(Connection con) {
        super(con, new UserMapper());
    }

    @Override
    public String getTable() {
        return "users";
    }

    @Override
    public void setPrimaryKey(User user, int id) {
        user.setId(id);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
            "id",
            "name",
            "email",
            "password",
            "role",
            "fecha_creacion"
        };
    }

    @Override
    public Object[] getInsertValues(User user) {
        return new Object[] {
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            user.getFechaCreacion()
        };
    }

    @Override
    public Object[] getUpdateValues(User user) {
        return new Object[] {
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            user.getFechaCreacion(),
            user.getId()
        };
    }

    //  Buscar por email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserMapper(), email));
    }

    // Respuesta simplificada
    public Optional<UserResponse> findResponseById(int id) {
        String sql = "SELECT id, name, email, role FROM users WHERE id = ?";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserResponseMapper(), id));
    }

    public List<UserResponse> findAllResponses() {
        String sql = "SELECT id, name, email, role FROM users";
        return DB.queryMany(con, sql, new UserResponseMapper());
    }
}