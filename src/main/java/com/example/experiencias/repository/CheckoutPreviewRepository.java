package com.example.experiencias.repository;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.example.experiencias.entity.CheckoutPreview;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.exception.NotFoundException;
import com.example.experiencias.mapper.RowMapper;

import database.DB;

public class CheckoutPreviewRepository extends BaseRepository<CheckoutPreview> {

    public CheckoutPreviewRepository(Connection con) {
        super(con, rs -> new CheckoutPreview(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("data"),
            rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    public CheckoutPreviewRepository(Connection con, RowMapper<CheckoutPreview> mapper) {
        super(con, mapper);
    }

    @Override public String getTable() { return "checkout_preview"; }
    @Override public void setPrimaryKey(CheckoutPreview cp, int id) { cp.setId(id); }

    @Override
    public String[] getColumnNames() {
        return new String[]{ "id", "user_id", "data", "created_at" };
    }

    @Override
    public Object[] getInsertValues(CheckoutPreview cp) {
        return new Object[]{ cp.getUserId(), cp.getData(), cp.getCreatedAt() };
    }

    @Override
    public Object[] getUpdateValues(CheckoutPreview cp) {
        return new Object[]{ cp.getUserId(), cp.getData(), cp.getCreatedAt(), cp.getId() };
    }

    @Override
    public int insert(CheckoutPreview cp) {
        String sql = """
            INSERT INTO checkout_preview (user_id, data, created_at)
            VALUES (?, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql, cp.getUserId(), cp.getData(), cp.getCreatedAt());
        setPrimaryKey(cp, id);
        return id;
    }

    // Recupera el preview verificando que pertenece al usuario
    public CheckoutPreview findByIdAndUserIdOrThrow(int id, int userId) {
        String sql = """
            SELECT id, user_id, data, created_at
            FROM checkout_preview
            WHERE id = ? AND user_id = ?
        """;
        CheckoutPreview preview = DB.queryOne(con, sql, mapper, id, userId);
        if (preview == null) {
            throw new NotFoundException("Checkout preview no encontrado o no pertenece al usuario");
        }
        return preview;
    }

    public int deleteByUserId(int userId) {
        return DB.delete(con, "DELETE FROM checkout_preview WHERE user_id = ?", userId);
    }

    // Para la tarea programada: elimina previews más antiguos que la fecha dada
    public int deleteOlderThan(LocalDateTime fecha) {
        return DB.delete(con, "DELETE FROM checkout_preview WHERE created_at < ?", fecha);
    }
}
