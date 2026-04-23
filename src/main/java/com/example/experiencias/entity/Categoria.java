package com.example.experiencias.entity;

public class Categoria {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;

    public Categoria() {}

    public Categoria(Integer id, String nombre, String descripcion, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    public Integer getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    @Override
    public String toString() {
        return "Categoria [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
    }
}
