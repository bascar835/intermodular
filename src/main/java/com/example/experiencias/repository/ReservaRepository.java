package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.dto.ReservaResumen;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.mapper.ReservaMapper;
import com.example.experiencias.mapper.RowMapper;

import database.DB;

public class ReservaRepository extends BaseRepository<Reserva> {

    public ReservaRepository(Connection con) {
        super(con, new ReservaMapper());
    }

    public ReservaRepository(Connection con, RowMapper<Reserva> mapper) {
        super(con, mapper);
    }

    @Override
    public String getTable() {
        return "reservas";
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {
            "id",
            "usuario_id",
            "experiencia_id",
            "fecha_reserva",
            "numero_personas",
            "precio_total",
            "estado"
        };
    }

    @Override
    public void setPrimaryKey(Reserva r, int id) {
        r.setId(id);
    }

    @Override
    public Object[] getInsertValues(Reserva r) {
        return new Object[] {
            r.getUsuario_id(),
            r.getExperiencia_id(),
            r.getFecha_reserva(),
            r.getNumero_personas(),
            r.getPrecio_total(),
            r.getEstado()
        };
    }

    @Override
    public Object[] getUpdateValues(Reserva r) {
        return new Object[] {
            r.getUsuario_id(),
            r.getExperiencia_id(),
            r.getFecha_reserva(),
            r.getNumero_personas(),
            r.getPrecio_total(),
            r.getEstado(),
            r.getId()
        };
    }

    // filtrar por usuario-NUEVO
    public List<ReservaResumen> findByUsuario(int usuarioId) {
        String sql = """
            SELECT id, experiencia_id, fecha_reserva, numero_personas, precio_total, estado
            FROM reservas
            WHERE usuario_id = ?
            ORDER BY fecha_reserva DESC
        """;

        return DB.queryMany(con, sql, rs ->
            new ReservaResumen(
                rs.getInt("id"),
                rs.getInt("experiencia_id"),
                rs.getTimestamp("fecha_reserva").toLocalDateTime(),
                rs.getInt("numero_personas"),
                rs.getDouble("precio_total"),
                rs.getString("estado")
            ),
            usuarioId
        );
    }
}
