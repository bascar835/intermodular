package com.example.experiencias.repository;

import java.sql.Connection;

import com.example.experiencias.entity.Director;
import com.example.experiencias.mapper.DirectorMapper;
import com.example.experiencias.mapper.RowMapper;

public class DirectorRepository extends BaseRepository<Director> {

	public DirectorRepository(Connection con) {
		super(con, new DirectorMapper());
	}

	public DirectorRepository(Connection con, RowMapper<Director> mapper) {
		super(con, mapper);
	}

	@Override
	public String getTable() {
		return "directores";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { "id", "nombre", "pais" };
	}
	
	@Override
	public void setPrimaryKey(Director p, int id) {
		p.setId(id);
	}

	@Override
	public Object[] getInsertValues(Director d) {
		return new Object[] { d.getNombre(), d.getPais() };
	}

	@Override
	public Object[] getUpdateValues(Director d) {
		return new Object[] { d.getNombre(), d.getPais(), d.getId() };
	}
}
