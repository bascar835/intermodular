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

    @Override public String getTable() { return "categorias"; }

    @Override
    public String[] getColumnNames() {
        return new String[] { "id", "nombre", "descripcion", "imagen_url", "activo" };
    }

    @Override public void setPrimaryKey(Categoria c, int id) { c.setId(id); }

    @Override
    public Object[] getInsertValues(Categoria c) {
        return new Object[] { c.getNombre(), c.getDescripcion(), c.getImagenUrl(), c.isActivo() };
    }

    @Override
    public Object[] getUpdateValues(Categoria c) {
        return new Object[] { c.getNombre(), c.getDescripcion(), c.getImagenUrl(), c.isActivo(), c.getId() };
    }

    @Override
    public int insert(Categoria c) {
        String sql = """
            INSERT INTO categorias (nombre, descripcion, imagen_url, activo)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql,
            c.getNombre(), c.getDescripcion(), c.getImagenUrl(), c.isActivo());
        setPrimaryKey(c, id);
        return id;
    }

    @Override
    public int update(Categoria c) {
        String sql = """
            UPDATE categorias
            SET nombre = ?, descripcion = ?, imagen_url = ?, activo = ?
            WHERE id = ?
        """;
        return DB.update(con, sql,
            c.getNombre(), c.getDescripcion(), c.getImagenUrl(), c.isActivo(), c.getId());
    }

    // Todas las categorías (admin)
    public List<CategoriaResumen> findResumen() {
        String sql = """
            SELECT id, nombre, descripcion, imagen_url, activo
            FROM categorias
            ORDER BY nombre
        """;
        return DB.queryMany(con, sql, rs -> new CategoriaResumen(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("imagen_url"),
            rs.getBoolean("activo")
        ));
    }

    // Solo categorías activas (frontend público)
    public List<CategoriaResumen> findResumenActivas() {
        String sql = """
            SELECT id, nombre, descripcion, imagen_url, activo
            FROM categorias
            WHERE activo = true
            ORDER BY nombre
        """;
        return DB.queryMany(con, sql, rs -> new CategoriaResumen(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("imagen_url"),
            rs.getBoolean("activo")
        ));
    }

    public List<ExperienciaResumen> findExperienciasPorCategoria(Long categoriaId) {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio,
                   e.ubicacion, e.duracion_horas, e.categoria_id, e.fecha_creacion,
                   (SELECT url FROM experiencia_imagenes WHERE experiencia_id = e.id ORDER BY id ASC LIMIT 1) AS imagen,
                   c.nombre AS categoria_nombre
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            WHERE e.categoria_id = ?
            ORDER BY e.titulo
        """;
        return DB.queryMany(con, sql, rs -> new ExperienciaResumen(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            (int) rs.getDouble("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getTimestamp("fecha_creacion") != null
                ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
            rs.getString("imagen"),
            rs.getString("categoria_nombre")
        ), categoriaId);
    }
}
