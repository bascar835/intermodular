package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.db.DB;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.dto.PeliculaDetalle;
import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.mapper.ExperienciaMapper;
import com.example.experiencias.mapper.RowMapper;



public class ExperienciaRepository extends BaseRepository<Experiencia> {

	public ExperienciaRepository(Connection con) {
		super(con, new ExperienciaMapper());
	}

	public ExperienciaRepository(Connection con, RowMapper<Experiencia> mapper) {
		super(con, mapper);
	}

	@Override
	public String getTable() {
		return "experiencias";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] { "id", "titulo", "descripcion", "precio", "ubicacion", "duracion_horas", "categoria_id", "activa", "fecha_creacion" };
	}
	
	@Override
	public void setPrimaryKey(Experiencia e, int id) {
		e.setId(id);
	}

	@Override
	public Object[] getInsertValues(Experiencia e) {
		return new Object[] { e.getTitulo(), e.getDescripcion(), e.getPrecio(),e.getUbicacion(),e.getDuracion_horas(),e.getCategoria_id(),e.isActiva(),e.getFecha_creacion() };
	}

	@Override
	public Object[] getUpdateValues(Experiencia e) {
		return new Object[] { e.getTitulo(), e.getDescripcion(), e.getPrecio(),e.getUbicacion(),e.getDuracion_horas(),e.getCategoria_id(),e.isActiva(),e.getFecha_creacion()};
	}

	public List<ExperienciaResumen> findResumen() {
		String sql = """
			SELECT id, nombre,descripcion,activa
			FROM categorias
			ORDER BY nombre
		""";
		
		return DB.queryMany(con, sql, rs ->
			new ExperienciaResumen(
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
			FROM Experiencias p
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
