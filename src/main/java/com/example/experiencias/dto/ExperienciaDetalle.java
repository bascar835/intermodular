package com.example.experiencias.dto;

import java.math.BigDecimal;

// DTO para la página de detalle — mismos campos que ExperienciaResumen
// pero separado para poder añadir más campos en el futuro (imágenes, valoraciones…)
public record ExperienciaDetalle(
    int id,
    String titulo,
    String descripcion,
    int precio,
    String ubicacion,
    int duracion_horas,
    int categoria_id,
    String categoria_nombre
) {}
