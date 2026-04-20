package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;


import com.example.experiencias.db.DB;
import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.mapper.UserRowMapper;
import com.example.experiencias.mapper.UserResponseRowMapper;

public class UserRepository extends BaseRepository<User> {

    public UserRepository(Connection con) {
        super(con, new UserRowMapper());
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

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        User user = DB.queryOne(con, sql, new UserRowMapper(), email);
        return Optional.ofNullable(user);
    }

    public Optional<UserResponse> findResponseById(int id) {
        String sql = """
            SELECT id, nombre, email, rol
            FROM usuarios
            WHERE id = ?
        """;
        UserResponse user = DB.queryOne(
            con,
            sql,
            new UserResponseRowMapper(),
            id
        );
        return Optional.ofNullable(user);
    }

    public List<UserResponse> findAllResponses() {
        String sql = """
            SELECT id, nombre, email, rol
            FROM usuarios
        """;
        return DB.queryMany(
            con,
            sql,
            new UserResponseRowMapper()
        );
    }
}
