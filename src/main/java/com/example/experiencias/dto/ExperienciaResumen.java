package com.example.experiencias.dto;

import java.time.LocalDateTime;

public record ExperienciaResumen(
    int id,
    String titulo,
    String descripcion,
    double precio,
    String ubicacion,
    int duracion_horas,
    int categoria_id,
    String categoria_nombre
	
    
    
) {}
