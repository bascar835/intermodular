package com.example.experiencias.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Connection;

import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.exception.DuplicateKeyException;
import com.example.experiencias.exception.NotFoundException;
import com.example.experiencias.mapper.RowMapper;

import database.DB;

public abstract class BaseRepository<T> {

    protected Connection con;
    protected RowMapper<T> mapper;

    protected BaseRepository(Connection con, RowMapper<T> mapper) {
        this.con = con;
        this.mapper = mapper;
    }

    public abstract String getTable();

    public String getPrimaryKeyName() {
        return "id";
    }

    public abstract void setPrimaryKey(T instance, int id);

    public abstract String[] getColumnNames();

    public abstract Object[] getInsertValues(T instance);

    public abstract Object[] getUpdateValues(T instance);

    // ── Queries ───────────────────────────────────────────────────────────────

    public T find(int id) {
        String sql = "SELECT * FROM " + getTable() + " WHERE " + getPrimaryKeyName() + " = ?";
        return DB.queryOne(con, sql, mapper, id);
    }

    public T findOrThrow(int id) {
        T obj = find(id);
        if (obj == null) throw new NotFoundException("Registro no encontrado con id: " + id);
        return obj;
    }

    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTable();
        return DB.queryMany(con, sql, mapper, new Object[0]);
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    public int insert(T instance) {
        String sql = buildInsertSql();
        int id = DB.insert(con, sql, getInsertValues(instance));
        setPrimaryKey(instance, id);
        return id;
    }

    public int update(T instance) {
        String sql = buildUpdateSql();
        return DB.update(con, sql, getUpdateValues(instance));
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM " + getTable() + " WHERE " + getPrimaryKeyName() + " = ?";
        return DB.delete(con, sql, id) == 1;
    }

    // ── SQL builders ──────────────────────────────────────────────────────────

    private String buildInsertSql() {
        List<String> columns = new ArrayList<>(List.of(getColumnNames()));
        columns.remove(getPrimaryKeyName());
        String columnsCsv = String.join(", ", columns);

        String[] placeholders = new String[columns.size()];
        Arrays.fill(placeholders, "?");
        String placeholdersCsv = String.join(", ", placeholders);

        return "INSERT INTO " + getTable() + " (" + columnsCsv + ") VALUES (" + placeholdersCsv + ")";
    }

    private String buildUpdateSql() {
        List<String> columns = new ArrayList<>(List.of(getColumnNames()));
        columns.remove(getPrimaryKeyName());

        List<String> assignments = new ArrayList<>();
        for (String column : columns) {
            assignments.add(column + " = ?");
        }

        return "UPDATE " + getTable() + " SET " + String.join(", ", assignments)
                + " WHERE " + getPrimaryKeyName() + " = ?";
    }

    // ── Exception translation ─────────────────────────────────────────────────

    protected RuntimeException translate(java.sql.SQLException e) {
        if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
            return new DuplicateKeyException("Clave duplicada", e);
        }
        return new DataAccessException(e);
    }
}
