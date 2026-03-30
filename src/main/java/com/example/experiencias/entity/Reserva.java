package com.example.experiencias.entity;

import java.time.LocalDateTime;

public class Reserva {

    private Integer id;
    private int usuario_id;
    private int experiencia_id;
    private LocalDateTime fecha_reserva;
    private int numero_personas;
    private Double precio_total;
    private String estado;

    // Constructor completo
    public Reserva(Integer id, int usuario_id, int experiencia_id, LocalDateTime fecha_reserva,
                   int numero_personas, Double precio_total, String estado) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.experiencia_id = experiencia_id;
        this.fecha_reserva = fecha_reserva;
        this.numero_personas = numero_personas;
        this.precio_total = precio_total;
        this.estado = estado;
    }

    // Constructor vacío (Spring y Mapper lo necesitan)
    public Reserva() {
    }

    public Integer getId() {
        return id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public int getExperiencia_id() {
        return experiencia_id;
    }

    public LocalDateTime getFecha_reserva() {
        return fecha_reserva;
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

    public void setExperiencia_id(int experiencia_id) {
        this.experiencia_id = experiencia_id;
    }

    public void setFecha_reserva(LocalDateTime fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
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
        return "Reserva [id=" + id + ", usuario_id=" + usuario_id + ", experiencia_id=" + experiencia_id
                + ", fecha_reserva=" + fecha_reserva + ", numero_personas=" + numero_personas
                + ", precio_total=" + precio_total + ", estado=" + estado + "]";
    }
}
