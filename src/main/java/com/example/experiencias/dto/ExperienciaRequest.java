package com.example.experiencias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

public record ExperienciaRequest(

    @NotBlank(message = "El título es obligatorio")
    String titulo,

    @NotBlank(message = "La descripción es obligatoria")
    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    Double precio,

    @NotBlank(message = "La ubicación es obligatoria")
    String ubicacion,

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    Integer duracion_horas,

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El ID de categoría debe ser válido")
    Integer categoria_id
) {}
