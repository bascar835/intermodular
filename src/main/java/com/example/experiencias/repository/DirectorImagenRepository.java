package com.example.experiencias.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import database.DB;

import com.example.experiencias.dto.ImagenResponse;
import com.example.experiencias.entity.DirectorImagen;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.mapper.RowMapper;
import com.example.experiencias.mapper.DirectorImagenMapper;

public class DirectorImagenRepository extends BaseRepository1<DirectorImagen> {

	public DirectorImagenRepository(Connection con) {
		super(con, new DirectorImagenMapper());
	}

	public DirectorImagenRepository(Connection con, RowMapper<DirectorImagen> mapper) {
		super(con, mapper);
	}

	@Override
	public String getTable() {
		return "director_imagenes";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { "id", "director_id", "url" };
	}
	
	@Override
	public Integer getPrimaryKey(DirectorImagen di) {
		return di.getId();
	}
	
	@Override
	public void setPrimaryKey(DirectorImagen di, int id) {
		di.setId(id);
	}

	@Override
	public Object[] getInsertValues(DirectorImagen di) {
		return new Object[] { di.getDirectorId(), di.getUrl() };
	}

	@Override
	public Object[] getUpdateValues(DirectorImagen di) {
		return new Object[] { di.getDirectorId(), di.getUrl(), di.getId() };
	}
	
	public List<ImagenResponse> findByDirectorId(int directorId) {

		String sql = """
					SELECT id, url
					FROM director_imagenes
					WHERE director_id = ?
					ORDER BY id ASC
				""";

		
			return DB.queryMany(con, sql, rs -> new ImagenResponse(
				rs.getInt("id"), 
				rs.getString("url")
			), directorId);
			
		
	}
}
