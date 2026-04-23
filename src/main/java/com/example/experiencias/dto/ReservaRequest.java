package com.example.experiencias.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ReservaRequest(

    @NotNull(message = "El ID de experiencia es obligatorio")
    @Positive(message = "El ID de experiencia debe ser válido")
    Integer experiencia_id,

    @NotNull(message = "La fecha de reserva es obligatoria")
    @Future(message = "La fecha de reserva debe ser futura")
    LocalDateTime fecha_reserva,

    @Min(value = 1, message = "El número de personas debe ser al menos 1")
    int numero_personas,

    @NotNull(message = "El precio total es obligatorio")
    @Positive(message = "El precio total debe ser mayor que 0")
    Double precio_total,

    @Pattern(
        regexp = "pendiente|confirmada|cancelada",
        message = "Estado inválido. Valores permitidos: pendiente, confirmada, cancelada"
    )
    String estado

) {}
