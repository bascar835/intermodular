package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.mapper.ExperienciaMapper;
import com.example.experiencias.mapper.RowMapper;


import database.DB;




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
        return new String[] { "id", "titulo", "descripcion", "precio", "ubicacion", "duracion_horas", "categoria_id", "fecha_creacion" };
    }

    @Override
    public void setPrimaryKey(Experiencia e, int id) {
        e.setId(id);
    }

    @Override
    public Object[] getInsertValues(Experiencia e) {
        return new Object[] { e.getTitulo(), e.getDescripcion(), e.getPrecio(), e.getUbicacion(), e.getDuracion_horas(), e.getCategoria_id(), e.getFecha_creacion() };
    }

    @Override
    public Object[] getUpdateValues(Experiencia e) {
        return new Object[] { e.getTitulo(), e.getDescripcion(), e.getPrecio(), e.getUbicacion(), e.getDuracion_horas(), e.getCategoria_id(), e.getFecha_creacion(), e.getId() };
    }

    public List<ExperienciaResumen> findResumen() {
        String sql = """
            SELECT id, titulo, descripcion, precio, ubicacion, duracion_horas, categoria_id, fecha_creacion
            FROM experiencias
            ORDER BY titulo
        """;

        return DB.queryMany(con, sql, rs ->
            new ExperienciaResumen(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getString("ubicacion"),
                rs.getInt("duracion_horas"),
                rs.getInt("categoria_id"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime()
            )
        );
    }
}
