package com.example.experiencias.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record CheckoutConfirmRequest(
    @NotNull(message = "El previewId es obligatorio")
    Integer previewId,

    // Lista de fechas elegidas por el usuario en el modal (una por experiencia)
    List<FechaItem> fechas
) {
    public record FechaItem(
        int experienciaId,
        String fecha   // ISO-8601: "2026-05-20T10:30"
    ) {}
}
