package com.example.experiencias.dto;

import java.time.LocalDateTime;

// DTO para listados — incluye solo la primera imagen (más ligero)
public record ExperienciaResumen(
    int id,
    String titulo,
    String descripcion,
    double precio,
    String ubicacion,
    int duracion_horas,
    int categoria_id,
    LocalDateTime fecha_creacion,
    String imagen,           // primera imagen de experiencia_imagenes (puede ser null)
    String categoria_nombre
) {}
