package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.Categoria;

public class CategoriaMapper implements RowMapper<Categoria> {
    @Override
    public Categoria map(ResultSet rs) throws SQLException {
        return new Categoria(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
}
