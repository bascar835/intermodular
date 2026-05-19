package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class Experiencia {

	private Integer id;
	private String titulo;
	private String descripcion;
	private double precio;
	private String ubicacion;
	private int duracion_horas;
	private int categoria_id;
	private String recomendamos;
	private String incluye;
	private LocalDateTime fecha_creacion;

	public Experiencia() {
	}

	public Experiencia(Integer id, String titulo, String descripcion, double precio, String ubicacion,
			int duracion_horas, int categoria_id, LocalDateTime fecha_creacion) {
		this.id = id;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.precio = precio;
		this.ubicacion = ubicacion;
		this.duracion_horas = duracion_horas;
		this.categoria_id = categoria_id;
		this.fecha_creacion = fecha_creacion;
	}

	public Experiencia(Integer id, String titulo, String descripcion, double precio, String ubicacion,
			int duracion_horas, int categoria_id, String recomendamos, String incluye, LocalDateTime fecha_creacion) {
		this.id = id;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.precio = precio;
		this.ubicacion = ubicacion;
		this.duracion_horas = duracion_horas;
		this.categoria_id = categoria_id;
		this.recomendamos = recomendamos;
		this.incluye = incluye;
		this.fecha_creacion = fecha_creacion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public int getDuracion_horas() {
		return duracion_horas;
	}

	public void setDuracion_horas(int duracion_horas) {
		this.duracion_horas = duracion_horas;
	}

	public int getCategoria_id() {
		return categoria_id;
	}

	public void setCategoria_id(int categoria_id) {
		this.categoria_id = categoria_id;
	}

	public String getRecomendamos() {
		return recomendamos;
	}

	public void setRecomendamos(String recomendamos) {
		this.recomendamos = recomendamos;
	}

	public String getIncluye() {
		return incluye;
	}

	public void setIncluye(String incluye) {
		this.incluye = incluye;
	}

	public LocalDateTime getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(LocalDateTime fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	@Override
	public String toString() {
		return "Experiencia [id=" + id + ", titulo=" + titulo + ", precio=" + precio + "]";
	}
}