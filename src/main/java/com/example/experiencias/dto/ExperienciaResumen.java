package com.example.experiencias.dto;

import java.time.LocalDateTime;

public record ExperienciaResumen(
		 Integer id,
		 String titulo,
		 String descripcion,
		 double precio,
		 String ubicacion,
		 int duracion_horas,
		 int categoria_id,
		 boolean activa,
		 LocalDateTime fecha_creacion
) {}
