package com.example.experiencias.controller;

import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.repository.ExperienciaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST — apartado Experiencias
 *
 * Endpoints:
 *   GET  /api/experiencias          → todas las activas
 *   POST /api/experiencias/filtrar  → body: { "categoria_id": 1 }
 *   POST /api/experiencias/buscar   → body: { "q": "valencia" }
 *
 * SNAKE_CASE: el application.properties tiene SNAKE_CASE activo,
 * así que el frontend envía "categoria_id" (no "categoriaId")
 * y recibe "duracion_horas", "categoria_nombre", etc.
 */
@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaController {

    private final DataSource ds;

    // Spring inyecta el DataSource automáticamente desde application.properties
    public ExperienciaController(DataSource ds) {
        this.ds = ds;
    }

    // ── GET /api/experiencias ────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> index() {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            List<ExperienciaResumen> lista = repo.findAllActivas();
            return ResponseEntity.ok(Map.of(
                "ok",    true,
                "total", lista.size(),
                "data",  lista
            ));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "ok",     false,
                "error",  "Error de conexión con la BD",
                "detalle", e.getMessage()
            ));
        }
    }

    // ── POST /api/experiencias/filtrar ───────────────────────────────
    // Body JSON: { "categoria_id": 1 }
    // Con SNAKE_CASE activo el frontend envía "categoria_id"
    @PostMapping("/filtrar")
    public ResponseEntity<?> filtrar(@RequestBody Map<String, Object> body) {

        if (body == null || !body.containsKey("categoria_id")) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok",    false,
                "error", "categoria_id es obligatorio"
            ));
        }

        Long categoriaId;
        try {
            categoriaId = Long.valueOf(body.get("categoria_id").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok",    false,
                "error", "categoria_id debe ser un número"
            ));
        }

        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            List<ExperienciaResumen> lista = repo.findPorCategoria(categoriaId);
            return ResponseEntity.ok(Map.of(
                "ok",          true,
                "categoria_id", categoriaId,
                "total",       lista.size(),
                "data",        lista
            ));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "ok",     false,
                "error",  "Error de conexión con la BD",
                "detalle", e.getMessage()
            ));
        }
    }

    // ── POST /api/experiencias/buscar ────────────────────────────────
    // Body JSON: { "q": "valencia" }
    @PostMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestBody Map<String, Object> body) {

        if (body == null || !body.containsKey("q")) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok",    false,
                "error", "El campo 'q' es obligatorio"
            ));
        }

        String termino = body.get("q").toString().trim();

        // Si vacío → devolver todas
        if (termino.isEmpty()) {
            try (Connection con = ds.getConnection()) {
                ExperienciaRepository repo = new ExperienciaRepository(con);
                List<ExperienciaResumen> todas = repo.findAllActivas();
                return ResponseEntity.ok(Map.of(
                    "ok", true, "q", "", "total", todas.size(), "data", todas
                ));
            } catch (SQLException e) {
                return ResponseEntity.internalServerError().body(Map.of(
                    "ok", false, "error", e.getMessage()
                ));
            }
        }

        if (termino.length() < 2) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false, "error", "Mínimo 2 caracteres"
            ));
        }

        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            List<ExperienciaResumen> lista = repo.buscarPorTexto(termino);
            return ResponseEntity.ok(Map.of(
                "ok", true, "q", termino,
                "total", lista.size(), "data", lista
            ));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "ok", false, "error", e.getMessage()
            ));
        }
    }
}
