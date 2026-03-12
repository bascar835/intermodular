package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.Director;

public class DirectorMapper implements RowMapper<Director> {
	@Override
    public Director map(ResultSet rs) throws SQLException {
        return new Director(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("pais")
        );
    }
}
