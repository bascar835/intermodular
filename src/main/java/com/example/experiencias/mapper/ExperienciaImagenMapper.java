package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.ExperienciaImagen;

public class ExperienciaImagenMapper implements RowMapper<ExperienciaImagen> {
    @Override
    public ExperienciaImagen map(ResultSet rs) throws SQLException {
        return new ExperienciaImagen(
            rs.getInt("id"),
            rs.getInt("experiencia_id"),
            rs.getString("url")
        );
    }
}
