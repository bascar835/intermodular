package com.example.experiencias.dto;

// DTO de consulta del carrito: incluye datos de la experiencia y del item
// para poder detectar cambios de precio entre lo guardado y el precio actual
public record CarritoItemDetalle(
    int id,
    int experienciaId,
    String titulo,
    double precioCarrito,   // snapshot guardado cuando se añadió
    double precioActual,    // precio actual de la experiencia
    int personas
) {}
