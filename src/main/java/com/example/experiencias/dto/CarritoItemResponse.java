package com.example.experiencias.dto;

public record CarritoItemResponse(
    int id,
    int experienciaId,
    int personas,
    double precio
) {}
