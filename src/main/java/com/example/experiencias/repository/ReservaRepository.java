package com.example.experiencias.repository;

import java.sql.Connection;

import com.example.experiencias.entity.Reserva;
import com.example.experiencias.mapper.ReservaMapper;
import com.example.experiencias.mapper.RowMapper;

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
		return new String[] { "id", "usuario_id", "experiencia_id", "fecha_reserva", "numero_personas", "precio_total",
				"estado" };
	}

	@Override
	public void setPrimaryKey(Reserva r, int id) {
		r.setId(id);
	}

	@Override
	public Object[] getInsertValues(Reserva r) {
		return new Object[] { r.getUsuario_id(), r.getData_reserva(), r.getNumero_personas(), r.getPrecio_total(),
				r.getEstado() };
	}

	@Override
	public Object[] getUpdateValues(Reserva r) {
		return new Object[] { r.getUsuario_id(), r.getData_reserva(), r.getNumero_personas(), r.getPrecio_total(),
				r.getEstado(), r.getId() };
	}
}
