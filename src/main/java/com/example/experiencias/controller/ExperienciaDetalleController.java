package com.example.experiencias.controller;

import com.example.experiencias.dto.ExperienciaDetalle;
import com.example.experiencias.repository.ExperienciaDetalleRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaDetalleController {

    private final DataSource ds;

    public ExperienciaDetalleController(DataSource ds) {
        this.ds = ds;
    }

    // ── GET /api/experiencias/{id} ───────────────────────────────────
    // El frontend llama a este endpoint con el id de la URL:
    //   /experiencias/show.html?id=1  →  fetch('/api/experiencias/1')
    //
    // Respuestas:
    //   200 → { ok: true, data: { id, titulo, precio, … } }
    //   404 → { ok: false, error: "Experiencia no encontrada" }
    //   500 → { ok: false, error: "Error de conexión" }
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaDetalleRepository repo = new ExperienciaDetalleRepository(con);
            ExperienciaDetalle exp = repo.findById(id);

            if (exp == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "ok",    false,
                    "error", "Experiencia no encontrada"
                ));
            }

            return ResponseEntity.ok(Map.of(
                "ok",   true,
                "data", exp
            ));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "ok",     false,
                "error",  "Error de conexión con la BD",
                "detalle", e.getMessage()
            ));
        }
    }
}
