package com.example.experiencias.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.example.experiencias.dto.CarritoItemDetalle;
import com.example.experiencias.entity.CarritoItem;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.exception.NotFoundException;
import com.example.experiencias.mapper.RowMapper;

import database.DB;

public class CarritoItemRepository extends BaseRepository<CarritoItem> {

    public CarritoItemRepository(Connection con) {
        super(con, rs -> new CarritoItem(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("experiencia_id"),
            rs.getInt("personas"),
            rs.getDouble("precio")
        ));
    }

    public CarritoItemRepository(Connection con, RowMapper<CarritoItem> mapper) {
        super(con, mapper);
    }

    @Override public String getTable()             { return "carrito_items"; }
    @Override public void setPrimaryKey(CarritoItem ci, int id) { ci.setId(id); }

    @Override
    public String[] getColumnNames() {
        return new String[]{ "id", "user_id", "experiencia_id", "personas", "precio" };
    }

    @Override
    public Object[] getInsertValues(CarritoItem ci) {
        return new Object[]{ ci.getUserId(), ci.getExperienciaId(), ci.getPersonas(), ci.getPrecio() };
    }

    @Override
    public Object[] getUpdateValues(CarritoItem ci) {
        return new Object[]{ ci.getUserId(), ci.getExperienciaId(), ci.getPersonas(), ci.getPrecio(), ci.getId() };
    }

    @Override
    public int insert(CarritoItem ci) {
        String sql = """
            INSERT INTO carrito_items (user_id, experiencia_id, personas, precio)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql,
            ci.getUserId(), ci.getExperienciaId(), ci.getPersonas(), ci.getPrecio());
        setPrimaryKey(ci, id);
        return id;
    }

    @Override
    public int update(CarritoItem ci) {
        String sql = """
            UPDATE carrito_items
            SET personas = ?, precio = ?
            WHERE id = ?
        """;
        return DB.update(con, sql, ci.getPersonas(), ci.getPrecio(), ci.getId());
    }

    // JOIN con experiencias para obtener titulo y precio actual
    public List<CarritoItemDetalle> findDetalleByUserId(int userId) {
        String sql = """
            SELECT
                ci.id,
                ci.experiencia_id,
                e.titulo,
                ci.precio        AS precio_carrito,
                e.precio         AS precio_actual,
                ci.personas
            FROM carrito_items ci
            JOIN experiencias e ON e.id = ci.experiencia_id
            WHERE ci.user_id = ?
            ORDER BY ci.id
        """;
        return DB.queryMany(con, sql, rs -> new CarritoItemDetalle(
            rs.getInt("id"),
            rs.getInt("experiencia_id"),
            rs.getString("titulo"),
            rs.getDouble("precio_carrito"),
            rs.getDouble("precio_actual"),
            rs.getInt("personas")
        ), userId);
    }

    // Busca un item concreto del usuario (para comprobar si ya existe al añadir)
    public CarritoItem findByUserIdAndExperienciaId(int userId, int experienciaId) {
        String sql = """
            SELECT id, user_id, experiencia_id, personas, precio
            FROM carrito_items
            WHERE user_id = ? AND experiencia_id = ?
        """;
        return DB.queryOne(con, sql, mapper, userId, experienciaId);
    }

    // Busca por id verificando que pertenece al usuario (seguridad)
    public CarritoItem findByIdAndUserIdOrThrow(int id, int userId) {
        String sql = """
            SELECT id, user_id, experiencia_id, personas, precio
            FROM carrito_items
            WHERE id = ? AND user_id = ?
        """;
        CarritoItem item = DB.queryOne(con, sql, mapper, id, userId);
        if (item == null) throw new NotFoundException("Item del carrito no encontrado");
        return item;
    }

    public int deleteByUserId(int userId) {
        return DB.delete(con, "DELETE FROM carrito_items WHERE user_id = ?", userId);
    }

    public void deleteByIdAndUserIdOrThrow(int id, int userId) {
        int rows = DB.delete(con, "DELETE FROM carrito_items WHERE id = ? AND user_id = ?", id, userId);
        if (rows == 0) throw new NotFoundException("Item del carrito no encontrado");
    }
}
