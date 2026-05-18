package com.example.experiencias.dto;

import java.time.LocalDateTime;

public record ReservaResponse(
    int id,
    int experienciaId,
    LocalDateTime fechaReserva,
    int numeroPersonas,
    double precioTotal,
    String estado,
    LocalDateTime fechaCreacion
) {}
