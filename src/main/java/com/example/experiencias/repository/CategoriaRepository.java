package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.dto.CategoriaResumen;
import com.example.experiencias.dto.PeliculaDetalle;
import com.example.experiencias.entity.Categoria;
import com.example.experiencias.mapper.CategoriaMapper;
import com.example.experiencias.mapper.RowMapper;

import database.DB;



public class CategoriaRepository extends BaseRepository<Categoria> {

	public CategoriaRepository(Connection con) {
		super(con, new CategoriaMapper());
	}

	public CategoriaRepository(Connection con, RowMapper<Categoria> mapper) {
		super(con, mapper);
	}

	@Override
	public String getTable() {
		return "categorias";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { "id", "nombre", "descripcion", "activa" };
	}
	
	@Override
	public void setPrimaryKey(Categoria c, int id) {
		c.setId(id);
	}

	@Override
	public Object[] getInsertValues(Categoria c) {
		return new Object[] { c.getNombre(), c.getDescripcion(), c.isActiva() };
	}

	@Override
	public Object[] getUpdateValues(Categoria c) {
		return new Object[] { c.getNombre(), c.getDescripcion(),c.isActiva(),c.getId() };
	}

	public List<CategoriaResumen> findResumen() {
		String sql = """
			SELECT id, nombre,descripcion,activa
			FROM categorias
			ORDER BY nombre
		""";
		
		return DB.queryMany(con, sql, rs ->
			new CategoriaResumen(
				rs.getInt("id"),
				rs.getString("nombre"),
				rs.getString("descripcion"),
				rs.getBoolean("activa")
			)
		);
	}
	
	public PeliculaDetalle findDetalle(int id) {
		String sql = """
			SELECT p.titulo,
			       p.anyo,
			       p.duracion,
			       p.sinopsis,
			       d.nombre AS director
			FROM Categorias p
			JOIN directores d ON p.director_id = d.id
			WHERE p.id = ?
		""";
		
		return DB.queryOne(con, sql, rs ->
			new PeliculaDetalle(
				rs.getString("nombre"),
				rs.getInt("anyo"),
				rs.getInt("duracion"),
				rs.getString("sinopsis"),
				rs.getString("director")
			),
			id
		);
	}
}
