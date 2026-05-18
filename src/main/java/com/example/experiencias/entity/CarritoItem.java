package com.example.experiencias.entity;

public class CarritoItem {
    private Integer id;
    private int userId;
    private int experienciaId;
    private int personas;
    private double precio;

    public CarritoItem(Integer id, int userId, int experienciaId, int personas, double precio) {
        this.id = id; this.userId = userId; this.experienciaId = experienciaId;
        this.personas = personas; this.precio = precio;
    }

    public Integer getId()               { return id; }
    public void setId(Integer id)        { this.id = id; }
    public int getUserId()               { return userId; }
    public void setUserId(int userId)    { this.userId = userId; }
    public int getExperienciaId()        { return experienciaId; }
    public void setExperienciaId(int e)  { this.experienciaId = e; }
    public int getPersonas()             { return personas; }
    public void setPersonas(int p)       { this.personas = p; }
    public double getPrecio()            { return precio; }
    public void setPrecio(double p)      { this.precio = p; }

    @Override
    public String toString() {
        return "CarritoItem[id=" + id + ", userId=" + userId +
               ", experienciaId=" + experienciaId + ", personas=" + personas + ", precio=" + precio + "]";
    }
}
