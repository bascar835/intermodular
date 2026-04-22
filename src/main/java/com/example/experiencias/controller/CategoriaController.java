package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.CategoriaResumen;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.CategoriaRepository;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final DataSource ds;

    public CategoriaController(DataSource ds) {
        this.ds = ds;
    }

    // GET /api/categorias
    @GetMapping
    public List<CategoriaResumen> index() {
        try (Connection con = ds.getConnection()) {
            return new CategoriaRepository(con).findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/categorias/experiencias  → body: { "categoriaId": 1 }
    @PostMapping("/experiencias")
    public ResponseEntity<?> experienciasPorCategoria(@RequestBody Map<String, Object> body) {

        if (body == null || !body.containsKey("categoriaId")) {
            return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "error", "categoriaId es obligatorio"
            ));
        }

        Long categoriaId = Long.valueOf(body.get("categoriaId").toString());

        try (Connection con = ds.getConnection()) {
            List<ExperienciaResumen> experiencias =
                new CategoriaRepository(con).findExperienciasPorCategoria(categoriaId);

            return ResponseEntity.ok(Map.of(
                "ok", true,
                "categoriaId", categoriaId,
                "total", experiencias.size(),
                "data", experiencias
            ));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
