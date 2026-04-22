package com.example.experiencias.dto;

import java.util.List;

// DTO para show/edit — incluye todas las imágenes como lista
public record ExperienciaDetalle(
    int id,
    String titulo,
    String descripcion,
    double precio,
    String ubicacion,
    int duracion_horas,
    int categoria_id,
    String categoria_nombre,
    List<ImagenResponse> imagenes
) {}
