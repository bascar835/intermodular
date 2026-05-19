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
            (int) rs.getDouble("duracion_horas"),   // numeric(4,2) en PG → castear via double
            rs.getInt("categoria_id"),
            rs.getString("recomendamos"),
            rs.getString("incluye"),
            rs.getTimestamp("fecha_creacion") != null
                ? rs.getTimestamp("fecha_creacion").toLocalDateTime()
                : null
        );
    }
}
