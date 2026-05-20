package com.example.experiencias.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.example.experiencias.entity.Pago;

public class PagoMapper implements RowMapper<Pago> {

    @Override
    public Pago map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_pago");
        return new Pago(
            rs.getInt("id"),
            rs.getInt("usuario_id"),
            rs.getString("referencia"),
            rs.getString("metodo_pago"),
            rs.getString("estado_pago"),
            rs.getDouble("importe_total"),
            ts != null ? ts.toLocalDateTime() : null
        );
    }
}
