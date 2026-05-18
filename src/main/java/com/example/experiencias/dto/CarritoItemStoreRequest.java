package com.example.experiencias.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarritoItemStoreRequest(
    @NotNull(message = "El ID de experiencia es obligatorio")
    Integer experienciaId,

    @NotNull(message = "El número de personas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    Integer personas
) {}
