package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
=======

import com.example.experiencias.db.DB;
>>>>>>> 0407c7245f949afc7920d5cfd9e1801539bba996
import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.mapper.UserMapper;
import com.example.experiencias.mapper.UserResponseMapper;

import database.DB;

public class UserRepository extends BaseRepository<User> {

    public UserRepository(Connection con) {
<<<<<<< HEAD
        super(con, new UserMapper());
=======
        super(con, new UserRowMapper());
>>>>>>> 0407c7245f949afc7920d5cfd9e1801539bba996
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

<<<<<<< HEAD
    //  Buscar por email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserMapper(), email));
=======
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        User user = DB.queryOne(con, sql, new UserRowMapper(), email);
        return Optional.ofNullable(user);
>>>>>>> 0407c7245f949afc7920d5cfd9e1801539bba996
    }

    // Respuesta simplificada
    public Optional<UserResponse> findResponseById(int id) {
<<<<<<< HEAD
        String sql = "SELECT id, name, email, role FROM users WHERE id = ?";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserResponseMapper(), id));
    }

    public List<UserResponse> findAllResponses() {
        String sql = "SELECT id, name, email, role FROM users";
        return DB.queryMany(con, sql, new UserResponseMapper());
=======
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
>>>>>>> 0407c7245f949afc7920d5cfd9e1801539bba996
    }
}
