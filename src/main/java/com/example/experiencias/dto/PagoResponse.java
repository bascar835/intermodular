package com.example.experiencias.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta devuelta al cliente tras completar la simulación de pago.
 *
 * Incluye los datos del pago y la lista de reservas creadas/confirmadas.
 */
public record PagoResponse(
    int            pagoId,
    String         referencia,
    String         metodoPago,
    String         estadoPago,
    double         importeTotal,
    LocalDateTime  fechaPago,
    List<ReservaResponse> reservas
) {}
