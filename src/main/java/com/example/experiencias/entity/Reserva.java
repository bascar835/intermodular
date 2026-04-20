package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class Reserva {

	private Integer id;
	private int usuario_id;
	private LocalDateTime data_reserva;
	private int numero_personas;
	private Double precio_total;
	private String estado;

	//CONSTRUCTORES
	public Reserva(Integer id, int usuario_id, LocalDateTime data_reserva, int numero_personas, Double precio_total,
			String estado) {
		super();
		this.id = id;
		this.usuario_id = usuario_id;
		this.data_reserva = data_reserva;
		this.numero_personas = numero_personas;
		this.precio_total = precio_total;
		this.estado = estado;
	}

	public Reserva() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public int getUsuario_id() {
		return usuario_id;
	}

	public LocalDateTime getData_reserva() {
		return data_reserva;
	}

	public int getNumero_personas() {
		return numero_personas;
	}

	public Double getPrecio_total() {
		return precio_total;
	}

	public String getEstado() {
		return estado;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}

	public void setData_reserva(LocalDateTime data_reserva) {
		this.data_reserva = data_reserva;
	}

	public void setNumero_personas(int numero_personas) {
		this.numero_personas = numero_personas;
	}

	public void setPrecio_total(Double precio_total) {
		this.precio_total = precio_total;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Reserva [id=" + id + ", usuario_id=" + usuario_id + ", data_reserva=" + data_reserva
				+ ", numero_personas=" + numero_personas + ", precio_total=" + precio_total + ", estado=" + estado
				+ "]";
	}
}
