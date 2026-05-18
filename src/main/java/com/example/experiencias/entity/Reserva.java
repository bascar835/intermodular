package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class Reserva {
    private Integer id;
    private Integer usuarioId;
    private Integer experienciaId;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaCreacion;
    private int numeroPersonas;
    private double precioTotal;
    private String estado;

    public Reserva() {}

    // Constructor SIN fechaCreacion — para crear reservas nuevas.
    // La BD la rellena automáticamente con DEFAULT NOW().
    public Reserva(Integer id, Integer usuarioId, Integer experienciaId,
                   LocalDateTime fechaReserva,
                   int numeroPersonas, double precioTotal, String estado) {
        this.id             = id;
        this.usuarioId      = usuarioId;
        this.experienciaId  = experienciaId;
        this.fechaReserva   = fechaReserva;
        this.fechaCreacion  = null;
        this.numeroPersonas = numeroPersonas;
        this.precioTotal    = precioTotal;
        this.estado         = estado;
    }

    // Constructor CON fechaCreacion — para leer reservas existentes del ResultSet.
    public Reserva(Integer id, Integer usuarioId, Integer experienciaId,
                   LocalDateTime fechaReserva, LocalDateTime fechaCreacion,
                   int numeroPersonas, double precioTotal, String estado) {
        this.id             = id;
        this.usuarioId      = usuarioId;
        this.experienciaId  = experienciaId;
        this.fechaReserva   = fechaReserva;
        this.fechaCreacion  = fechaCreacion;
        this.numeroPersonas = numeroPersonas;
        this.precioTotal    = precioTotal;
        this.estado         = estado;
    }

    public Integer getId()                           { return id; }
    public void setId(Integer id)                    { this.id = id; }
    public Integer getUsuario_id()                   { return usuarioId; }
    public void setUsuario_id(Integer v)             { this.usuarioId = v; }
    public Integer getExperiencia_id()               { return experienciaId; }
    public void setExperiencia_id(Integer v)         { this.experienciaId = v; }
    public LocalDateTime getFecha_reserva()          { return fechaReserva; }
    public void setFecha_reserva(LocalDateTime v)    { this.fechaReserva = v; }
    public LocalDateTime getFecha_creacion()         { return fechaCreacion; }
    public void setFecha_creacion(LocalDateTime v)   { this.fechaCreacion = v; }
    public int getNumero_personas()                  { return numeroPersonas; }
    public void setNumero_personas(int v)            { this.numeroPersonas = v; }
    public double getPrecio_total()                  { return precioTotal; }
    public void setPrecio_total(double v)            { this.precioTotal = v; }
    public String getEstado()                        { return estado; }
    public void setEstado(String estado)             { this.estado = estado; }
}
