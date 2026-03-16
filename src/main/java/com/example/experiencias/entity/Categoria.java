package com.example.experiencias.entity;

public class Categoria {
	
	private Integer id;
	private String nombre;
	private String descripcion;
	private boolean activa;
	
	
	public Categoria() {
    }

	public Categoria(Integer id, String nombre, String descripcion, boolean activa) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.activa = activa;
	}
	public int getId() {
		return id;
	}
	public String getNombre() {
		return nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	@Override
	public String toString() {
		return "Categorias [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", activa=" + activa
				+ "]";
	}
	
	
}
