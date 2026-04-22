package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.dto.ExperienciaDetalle;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.dto.ImagenResponse;
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
        // imagen_url eliminado: las imágenes van en experiencia_imagenes
        return new String[] { "id", "titulo", "descripcion", "precio",
                              "ubicacion", "duracion_horas", "categoria_id" };
    }

    @Override
    public void setPrimaryKey(Experiencia e, int id) {
        e.setId(id);
    }

    @Override
    public Object[] getInsertValues(Experiencia e) {
        return new Object[] {
            e.getTitulo(), e.getDescripcion(), e.getPrecio(),
            e.getUbicacion(), e.getDuracion_horas(), e.getCategoria_id()
        };
    }

    @Override
    public Object[] getUpdateValues(Experiencia e) {
        return new Object[] {
            e.getTitulo(), e.getDescripcion(), e.getPrecio(),
            e.getUbicacion(), e.getDuracion_horas(), e.getCategoria_id(),
            e.getId()
        };
    }

    @Override
    public int insert(Experiencia e) {
        String sql = """
            INSERT INTO experiencias (titulo, descripcion, precio, ubicacion, duracion_horas, categoria_id)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql, getInsertValues(e));
        setPrimaryKey(e, id);
        return id;
    }

    // Necesario para ExperienciaRepository.findOrThrow() (usado en ExperienciaImagenController)
    public Experiencia findOrThrow(int id) {
        String sql = "SELECT * FROM experiencias WHERE id = ?";
        Experiencia e = DB.queryOne(con, sql, mapper, id);
        if (e == null) {
            throw new com.example.experiencias.exception.NotFoundException("Experiencia no encontrada: " + id);
        }
        return e;
    }

    // ── Listado admin — une con primera imagen de experiencia_imagenes ─────────
    public List<ExperienciaResumen> findResumen() {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion,
                   e.duracion_horas, e.categoria_id, e.fecha_creacion,
                   c.nombre AS categoria_nombre,
                   (SELECT url FROM experiencia_imagenes
                    WHERE experiencia_id = e.id ORDER BY id ASC LIMIT 1) AS imagen
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
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
                    ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
                rs.getString("imagen"),
                rs.getString("categoria_nombre")
            )
        );
    }

    // ── Listado público ────────────────────────────────────────────────────────
    public List<ExperienciaResumen> findAllActivas() {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion,
                   e.duracion_horas, e.categoria_id, e.fecha_creacion,
                   c.nombre AS categoria_nombre,
                   (SELECT url FROM experiencia_imagenes
                    WHERE experiencia_id = e.id ORDER BY id ASC LIMIT 1) AS imagen
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            ORDER BY c.nombre, e.titulo
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
                    ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
                rs.getString("imagen"),
                rs.getString("categoria_nombre")
            )
        );
    }

    // ── Filtrar por categoría ──────────────────────────────────────────────────
    public List<ExperienciaResumen> findPorCategoria(Long categoriaId) {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion,
                   e.duracion_horas, e.categoria_id, e.fecha_creacion,
                   c.nombre AS categoria_nombre,
                   (SELECT url FROM experiencia_imagenes
                    WHERE experiencia_id = e.id ORDER BY id ASC LIMIT 1) AS imagen
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
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
                    ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
                rs.getString("imagen"),
                rs.getString("categoria_nombre")
            ), categoriaId);
    }

    // ── Búsqueda por texto libre ───────────────────────────────────────────────
    public List<ExperienciaResumen> buscarPorTexto(String termino) {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion,
                   e.duracion_horas, e.categoria_id, e.fecha_creacion,
                   c.nombre AS categoria_nombre,
                   (SELECT url FROM experiencia_imagenes
                    WHERE experiencia_id = e.id ORDER BY id ASC LIMIT 1) AS imagen
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            WHERE (
                e.titulo      ILIKE '%' || ? || '%'
             OR e.descripcion ILIKE '%' || ? || '%'
             OR e.ubicacion   ILIKE '%' || ? || '%'
            )
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
                    ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
                rs.getString("imagen"),
                rs.getString("categoria_nombre")
            ), termino, termino, termino);
    }

    // ── Detalle con todas las imágenes ─────────────────────────────────────────
    public ExperienciaDetalle findDetalle(int id) {
        String sql = """
            SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion,
                   e.duracion_horas, e.categoria_id,
                   c.nombre AS categoria_nombre
            FROM experiencias e
            JOIN categorias c ON c.id = e.categoria_id
            WHERE e.id = ?
        """;

        ExperienciaDetalle base = DB.queryOne(con, sql, rs -> new ExperienciaDetalle(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            rs.getInt("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getString("categoria_nombre"),
            null  // imagenes se cargan aparte
        ), id);

        if (base == null) return null;

        // Cargar la lista de imágenes
        List<ImagenResponse> imagenes = new ExperienciaImagenRepository(con)
                .findByExperienciaId(id);

        // Devolver el DTO completo con imágenes
        return new ExperienciaDetalle(
            base.id(), base.titulo(), base.descripcion(), base.precio(),
            base.ubicacion(), base.duracion_horas(), base.categoria_id(),
            base.categoria_nombre(), imagenes
        );
    }
}
