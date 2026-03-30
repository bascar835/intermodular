package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.dto.CategoriaResumen;
import com.example.experiencias.dto.ExperienciaResumen;
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
        return new String[] { "id", "nombre", "descripcion" };
    }

    @Override
    public void setPrimaryKey(Categoria c, int id) {
        c.setId(id);
    }

    @Override
    public Object[] getInsertValues(Categoria c) {
        return new Object[] { c.getNombre(), c.getDescripcion() };
    }

    @Override
    public Object[] getUpdateValues(Categoria c) {
        return new Object[] { c.getNombre(), c.getDescripcion(), c.getId() };
    }

    public List<CategoriaResumen> findResumen() {
        String sql = """
            SELECT id, nombre, descripcion
            FROM categorias
            ORDER BY nombre
        """;

        return DB.queryMany(con, sql, rs ->
            new CategoriaResumen(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion")
            )
        );
    }

    // ── HITO 2 ────────────────────────────────────────────────────────────────
    // Devuelve todas las experiencias que pertenecen a una categoría concreta.
    public List<ExperienciaResumen> findExperienciasPorCategoria(Long categoriaId) {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio,
                   e.ubicacion, e.duracion_horas, e.categoria_id, e.fecha_creacion
            FROM experiencias e
            WHERE e.categoria_id = ?
            ORDER BY e.titulo
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
                rs.getTimestamp("fecha_creacion") != null
                    ? rs.getTimestamp("fecha_creacion").toLocalDateTime()
                    : null
            ),
            categoriaId
        );
    }
}
