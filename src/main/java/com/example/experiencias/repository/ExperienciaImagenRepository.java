package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import database.DB;
import com.example.experiencias.dto.ImagenResponse;
import com.example.experiencias.entity.ExperienciaImagen;
import com.example.experiencias.mapper.ExperienciaImagenMapper;
import com.example.experiencias.mapper.RowMapper;

public class ExperienciaImagenRepository extends BaseRepository<ExperienciaImagen> {

    public ExperienciaImagenRepository(Connection con) {
        super(con, new ExperienciaImagenMapper());
    }

    public ExperienciaImagenRepository(Connection con, RowMapper<ExperienciaImagen> mapper) {
        super(con, mapper);
    }

    @Override
    public String getTable() {
        return "experiencia_imagenes";
    }

    @Override
    public String[] getColumnNames() {
        return new String[] { "id", "experiencia_id", "url" };
    }

    @Override
    public void setPrimaryKey(ExperienciaImagen ei, int id) {
        ei.setId(id);
    }

    @Override
    public Object[] getInsertValues(ExperienciaImagen ei) {
        return new Object[] { ei.getExperienciaId(), ei.getUrl() };
    }

    @Override
    public Object[] getUpdateValues(ExperienciaImagen ei) {
        return new Object[] { ei.getExperienciaId(), ei.getUrl(), ei.getId() };
    }

    @Override
    public int insert(ExperienciaImagen ei) {
        String sql = """
            INSERT INTO experiencia_imagenes (experiencia_id, url)
            VALUES (?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql, ei.getExperienciaId(), ei.getUrl());
        setPrimaryKey(ei, id);
        return id;
    }

    public List<ImagenResponse> findByExperienciaId(int experienciaId) {
        String sql = """
                SELECT id, url
                FROM experiencia_imagenes
                WHERE experiencia_id = ?
                ORDER BY id ASC
                """;

        return DB.queryMany(con, sql, rs -> new ImagenResponse(
            rs.getInt("id"),
            rs.getString("url")
        ), experienciaId);
    }
}
