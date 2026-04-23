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
            "fecha_registro",
            "deleted"
        };
    }

    @Override
    public Object[] getInsertValues(User user) {
        return new Object[] {
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            user.getFechaCreacion(),
            false
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
            user.getDeleted() != null ? user.getDeleted() : false,
            user.getId()
        };
    }

    // ✅ INSERT con cast explícito al ENUM de PostgreSQL + RETURNING id
    @Override
    public int insert(User user) {
        String sql = """
            INSERT INTO usuarios (nombre, email, password, rol, fecha_registro, deleted)
            VALUES (?, ?, ?, ?::rol_usuario, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql,
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            user.getFechaCreacion(),
            false
        );
        setPrimaryKey(user, id);
        return id;
    }

    // ✅ UPDATE con cast explícito al ENUM de PostgreSQL
    @Override
    public int update(User user) {
        String sql = """
            UPDATE usuarios
            SET nombre = ?, email = ?, password = ?, rol = ?::rol_usuario, fecha_registro = ?, deleted = ?
            WHERE id = ?
        """;
        return DB.update(con, sql, getUpdateValues(user));
    }

    // Buscar por email (solo activos)
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND deleted = false";
        return Optional.ofNullable(DB.queryOne(con, sql, new UserMapper(), email));
    }

    // Buscar user por id (incluyendo deleted, para operaciones internas)
    public User find(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        return DB.queryOne(con, sql, new UserMapper(), id);
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

    // Todos los usuarios activos como respuesta simplificada
    public List<UserResponse> findAllResponses() {
        String sql = """
            SELECT id, nombre, email, rol
            FROM usuarios
            WHERE deleted = false
        """;
        return DB.queryMany(con, sql, new UserResponseMapper());
    }

    // ✅ Verificar email único al crear (excluye deleted)
    public boolean emailExists(String email) {
        String sql = """
            SELECT COUNT(*)
            FROM usuarios
            WHERE email = ?
            AND deleted = false
        """;
        Integer total = DB.queryOne(con, sql, rs -> rs.getInt(1), email);
        return total != null && total > 0;
    }

    // ✅ Verificar email único al editar (excluye el propio usuario y deleted)
    public boolean emailExistsForOtherUser(String email, int id) {
        String sql = """
            SELECT COUNT(*)
            FROM usuarios
            WHERE email = ?
            AND id <> ?
            AND deleted = false
        """;
        Integer total = DB.queryOne(con, sql, rs -> rs.getInt(1), email, id);
        return total != null && total > 0;
    }

    // ✅ Contar admins activos (para evitar eliminar el último)
    public int countAdmins() {
        String sql = """
            SELECT COUNT(*)
            FROM usuarios
            WHERE rol = 'ROLE_ADMIN'::rol_usuario
            AND deleted = false
        """;
        Integer count = DB.queryOne(con, sql, rs -> rs.getInt(1));
        return count != null ? count : 0;
    }

    // ✅ Verificar si el usuario tiene reservas (usa columna usuario_id de peliculas-v4)
    public boolean hasReservas(int userId) {
        String sql = """
            SELECT COUNT(*)
            FROM reservas
            WHERE usuario_id = ?
        """;
        Integer total = DB.queryOne(con, sql, rs -> rs.getInt(1), userId);
        return total != null && total > 0;
    }

    // ✅ Migrar reservas de un usuario a otro
    public void migrarReservas(int origen, int destino) {
        String sql = """
            UPDATE reservas
            SET usuario_id = ?
            WHERE usuario_id = ?
        """;
        DB.update(con, sql, destino, origen);
    }

    // ✅ Soft delete: marca deleted=true en lugar de borrar
    public boolean softDelete(int id) {
        String sql = """
            UPDATE usuarios
            SET deleted = true
            WHERE id = ?
        """;
        return DB.update(con, sql, id) == 1;
    }
}
