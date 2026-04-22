package com.example.experiencias.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import database.DB;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.exception.DuplicateKeyException;
import com.example.experiencias.exception.NotFoundException;
import com.example.experiencias.mapper.RowMapper;

public abstract class BaseRepository1<T> {

	protected Connection con;
	protected RowMapper<T> mapper;

	protected BaseRepository1(Connection con, RowMapper<T> mapper) {
		this.con = con;
		this.mapper = mapper;
	}

	public abstract String getTable();

	public String getPrimaryKeyName() {
		return "id";
	}

	public abstract Integer getPrimaryKey(T instance);
	public abstract void setPrimaryKey(T instance, int id);

	public abstract String[] getColumnNames();

	public abstract Object[] getInsertValues(T instance);

	public abstract Object[] getUpdateValues(T instance);

	public T find(int id) {
		
			String sql = "SELECT * FROM " + getTable() + " WHERE " + getPrimaryKeyName() + " = ?";
			return DB.queryOne(con, sql, mapper, id);
		
	}
	
	public T findOrThrow(int id) {
		
			String sql = "SELECT * FROM " + getTable() + " WHERE " + getPrimaryKeyName() + " = ?";
			T obj = DB.queryOne(con, sql, mapper, id);
			NotFoundException.ifNull(obj, "Registro no encontrado");
			return obj;
		
	}

	public List<T> findAll() {	
		
			String sql = "SELECT * FROM " + getTable();
			return DB.queryMany(con, sql, mapper, new Object[0]);
		
	}

	public T insert(T instance) {
		
			String sql = buildInsertSql();
			int id = DB.insert(con, sql, getInsertValues(instance));
			setPrimaryKey(instance, id);
			return instance;
		
	}

	public int update(T instance) {

		
			String sql = buildUpdateSql();
			return DB.update(con, sql, getUpdateValues(instance));
		
	}

	public int delete(int id) {
		
		
			String sql = "DELETE FROM  " + getTable() + " WHERE " + getPrimaryKeyName() + " = ?";
			return DB.delete(con, sql, id);
		
	}

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

		String set = String.join(", ", assignments);

		return "UPDATE " + getTable() + " SET " + set + " WHERE " + getPrimaryKeyName() + " = ?";
	}

	protected RuntimeException translate(SQLException e) {

		if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
			return new DuplicateKeyException("Clave duplicada", e);
		}

		return new DataAccessException(e);
	}
}
