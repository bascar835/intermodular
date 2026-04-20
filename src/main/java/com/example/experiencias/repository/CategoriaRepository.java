package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.db.DB;
import com.example.experiencias.dto.CategoriaResumen;
import com.example.experiencias.entity.Categoria;
import com.example.experiencias.mapper.CategoriaMapper;
import com.example.experiencias.mapper.RowMapper;

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
}
