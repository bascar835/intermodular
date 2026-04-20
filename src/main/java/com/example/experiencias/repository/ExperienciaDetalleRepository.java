package com.example.experiencias.repository;

import com.example.experiencias.db.DB;
import com.example.experiencias.dto.ExperienciaDetalle;

import java.sql.Connection;

public class ExperienciaDetalleRepository {

    private final Connection con;

    public ExperienciaDetalleRepository(Connection con) {
        this.con = con;
    }

    // ── findById() ───────────────────────────────────────────────────
    // Devuelve UNA experiencia completa por su id.
    // queryOne() devuelve null si no existe → el controller responde 404.
    public ExperienciaDetalle findById(int id) {
        String sql = "SELECT e.id, e.titulo, e.descripcion, e.precio, e.ubicacion, e.duracion_horas, e.categoria_id, c.nombre AS categoria_nombre FROM experiencias e JOIN categorias c ON c.id = e.categoria_id WHERE e.id = ?";

        return DB.queryOne(con, sql, rs -> new ExperienciaDetalle(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getInt("precio"),
            rs.getString("ubicacion"),
            rs.getInt("duracion_horas"),
            rs.getInt("categoria_id"),
            rs.getString("categoria_nombre")
        ), id);
    }
}
