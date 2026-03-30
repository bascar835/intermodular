package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.experiencias.entity.Reserva;

public class ReservaMapper implements RowMapper<Reserva> {

    @Override
    public Reserva map(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId(rs.getInt("id"));
        r.setUsuario_id(rs.getInt("usuario_id"));
        r.setData_reserva(rs.getTimestamp("data_reserva").toLocalDateTime());
        r.setNumero_personas(rs.getInt("numero_personas"));
        r.setPrecio_total(rs.getDouble("precio_total"));
        r.setEstado(rs.getString("estado"));
        return r;
    }
}
