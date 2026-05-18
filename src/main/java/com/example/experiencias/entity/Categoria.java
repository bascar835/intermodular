package com.example.experiencias.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Categoria {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private boolean activo;

    public Categoria() {}

    public Categoria(Integer id, String nombre, String descripcion, String imagenUrl, boolean activo) {
        this.id        = id;
        this.nombre    = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.activo    = activo;
    }

    public Integer getId()                      { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getNombre()                   { return nombre; }
    public void setNombre(String nombre)        { this.nombre = nombre; }
    public String getDescripcion()              { return descripcion; }
    public void setDescripcion(String d)        { this.descripcion = d; }
    public boolean isActivo()                   { return activo; }
    public void setActivo(boolean activo)       { this.activo = activo; }

    @JsonProperty("imagen_url")
    public String getImagenUrl()                { return imagenUrl; }
    public void setImagenUrl(String imagenUrl)  { this.imagenUrl = imagenUrl; }

    @Override
    public String toString() {
        return "Categoria [id=" + id + ", nombre=" + nombre + ", activo=" + activo + "]";
    }
}
