package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.ExperienciaDetalle;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ExperienciaRepository;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaController {

    private final DataSource ds;

    public ExperienciaController(DataSource ds) {
        this.ds = ds;
    }

    // ── GET /api/experiencias ────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> index() {
        try (Connection con = ds.getConnection()) {
            List<ExperienciaResumen> lista = new ExperienciaRepository(con).findAllActivas();
            return ResponseEntity.ok(Map.of(
                "ok",    true,
                "total", lista.size(),
                "data",  lista
            ));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── GET /api/experiencias/{id} ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable("id") int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaDetalle exp = new ExperienciaRepository(con).findDetalle(id);
            if (exp == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "ok",      false,
                    "error",   "Experiencia no encontrada",
                    "message", "Experiencia no encontrada"
                ));
            }
            return ResponseEntity.ok(Map.of("ok", true, "data", exp));
        } catch (Exception e) {
            // Log completo para diagnóstico
            System.err.println("[ExperienciaController.detalle] id=" + id + " ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "ok",      false,
                "message", "Error cargando experiencia: " + e.getMessage(),
                "causa",   e.getCause() != null ? e.getCause().getMessage() : "sin causa"
            ));
        }
    }

    // ── POST /api/experiencias/filtrar ───────────────────────────────
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
            List<ExperienciaResumen> lista = new ExperienciaRepository(con).findPorCategoria(categoriaId);
            return ResponseEntity.ok(Map.of(
                "ok",           true,
                "categoria_id", categoriaId,
                "total",        lista.size(),
                "data",         lista
            ));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── POST /api/experiencias/buscar ────────────────────────────────
    @PostMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("q")) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok",    false,
                "error", "El campo 'q' es obligatorio"
            ));
        }
        String termino = body.get("q").toString().trim();

        if (termino.isEmpty()) {
            try (Connection con = ds.getConnection()) {
                List<ExperienciaResumen> todas = new ExperienciaRepository(con).findAllActivas();
                return ResponseEntity.ok(Map.of("ok", true, "q", "", "total", todas.size(), "data", todas));
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }
        if (termino.length() < 2) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "Mínimo 2 caracteres"));
        }
        try (Connection con = ds.getConnection()) {
            List<ExperienciaResumen> lista = new ExperienciaRepository(con).buscarPorTexto(termino);
            return ResponseEntity.ok(Map.of(
                "ok", true, "q", termino,
                "total", lista.size(), "data", lista
            ));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
