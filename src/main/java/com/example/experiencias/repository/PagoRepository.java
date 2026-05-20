package com.example.experiencias.repository;

import java.sql.Connection;
import java.util.List;

import com.example.experiencias.entity.Pago;
import com.example.experiencias.mapper.PagoMapper;
import com.example.experiencias.mapper.RowMapper;

import database.DB;

public class PagoRepository extends BaseRepository<Pago> {

    public PagoRepository(Connection con) {
        super(con, new PagoMapper());
    }

    public PagoRepository(Connection con, RowMapper<Pago> mapper) {
        super(con, mapper);
    }

    @Override public String getTable() { return "pagos"; }

    @Override
    public String[] getColumnNames() {
        return new String[] {
            "id", "usuario_id", "referencia", "metodo_pago",
            "estado_pago", "importe_total", "fecha_pago"
        };
    }

    @Override public void setPrimaryKey(Pago p, int id) { p.setId(id); }

    @Override
    public Object[] getInsertValues(Pago p) {
        return new Object[] {
            p.getUsuarioId(), p.getReferencia(), p.getMetodoPago(),
            p.getEstadoPago(), p.getImporteTotal(), p.getFechaPago()
        };
    }

    @Override
    public Object[] getUpdateValues(Pago p) {
        return new Object[] {
            p.getUsuarioId(), p.getReferencia(), p.getMetodoPago(),
            p.getEstadoPago(), p.getImporteTotal(), p.getFechaPago(),
            p.getId()
        };
    }

    /** INSERT usando RETURNING id (compatible con PostgreSQL). */
    @Override
    public int insert(Pago p) {
        String sql = """
            INSERT INTO pagos (usuario_id, referencia, metodo_pago, estado_pago, importe_total, fecha_pago)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        int id = DB.insertReturning(con, sql,
            p.getUsuarioId(), p.getReferencia(), p.getMetodoPago(),
            p.getEstadoPago(), p.getImporteTotal(), p.getFechaPago()
        );
        setPrimaryKey(p, id);
        return id;
    }

    /** Pagos de un usuario ordenados por fecha descendente. */
    public List<Pago> findByUsuario(int usuarioId) {
        String sql = """
            SELECT * FROM pagos
            WHERE usuario_id = ?
            ORDER BY fecha_pago DESC
        """;
        return DB.queryMany(con, sql, new PagoMapper(), usuarioId);
    }
}
