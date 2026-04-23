package com.example.experiencias.dto;

import java.time.LocalDateTime;

public record ReservaResumen(int id, int experiencia_id, LocalDateTime fecha_reserva, int numero_personas,
		Double precio_total, String estado) {

}
