package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.Experiencia;

public class ExperienciaMapper implements RowMapper<Experiencia> {
    @Override
    public Experiencia map(ResultSet rs) throws SQLException {
        return new Experiencia(
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
        );
    }
}
