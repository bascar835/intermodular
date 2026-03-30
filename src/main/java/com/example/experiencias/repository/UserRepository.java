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
        return "usuarios";
    }

    @Override
    public void setPrimaryKey(User user, int id) {
        user.setId(id);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
            "id",
            "nombre",
            "email",
            "password",
            "rol",
            "fecha_registro"
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

    // ✅ INSERT con cast explícito al ENUM de PostgreSQL
    @Override
    public int insert(User user) {
        String sql = """
            INSERT INTO usuarios (nombre, email, password, rol, fecha_registro)
            VALUES (?, ?, ?, ?::rol_usuario, ?)
        """;
        int id = DB.insert(con, sql, getInsertValues(user));
        setPrimaryKey(user, id);
        return id;
    }

    // ✅ UPDATE con cast explícito al ENUM de PostgreSQL
    @Override
    public int update(User user) {
        String sql = """
            UPDATE usuarios
            SET nombre = ?, email = ?, password = ?, rol = ?::rol_usuario, fecha_registro = ?
            WHERE id = ?
        """;
        return DB.update(con, sql, getUpdateValues(user));
    }

    // Buscar por email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserMapper(), email));
    }

    // Respuesta simplificada por id
    public Optional<UserResponse> findResponseById(int id) {
        String sql = """
            SELECT id, nombre, email, rol
            FROM usuarios
            WHERE id = ?
        """;
        return Optional.ofNullable(DB.queryOne(con, sql, new UserResponseMapper(), id));
    }

    // Todos los usuarios como respuesta simplificada
    public List<UserResponse> findAllResponses() {
        String sql = """
            SELECT id, nombre, email, rol
            FROM usuarios
        """;
        return DB.queryMany(con, sql, new UserResponseMapper());
    }
}
