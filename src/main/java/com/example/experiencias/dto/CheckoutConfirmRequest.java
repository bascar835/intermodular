package com.example.experiencias.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de la petición POST /api/me/carrito/checkout/confirm.
 *
 * Campos añadidos respecto a la versión original:
 *  - metodoPago : método de pago simulado ("tarjeta", "transferencia", "paypal")
 */
public record CheckoutConfirmRequest(
    @NotNull(message = "El previewId es obligatorio")
    Integer previewId,

    // Lista de fechas elegidas por el usuario en el modal (una por experiencia)
    List<FechaItem> fechas,

    // ── PAGO SIMULADO ────────────────────────────────────────────────────────
    @NotBlank(message = "El método de pago es obligatorio")
    String metodoPago   // "tarjeta" | "transferencia" | "paypal"

) {
    public record FechaItem(
        int experienciaId,
        String fecha   // ISO-8601: "2026-05-20T10:30"
    ) {}
}
