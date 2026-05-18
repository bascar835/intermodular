package com.example.experiencias.dto;

public record CheckoutItemPreview(
    int experienciaId,
    String titulo,
    int personasAnterior,
    int personasFinal,
    double precioAnterior,
    double precioFinal,
    boolean hayCambios
) {}
