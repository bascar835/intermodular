package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.CategoriaFiltroDTO;
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

    // ── LISTADO DE CATEGORÍAS ─────────────────────────────
    @GetMapping
    public List<CategoriaResumen> index() {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            return repo.findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── EXPERIENCIAS POR CATEGORÍA (HITO 2 CORRECTO) ──────
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

            CategoriaRepository repo = new CategoriaRepository(con);

            List<ExperienciaResumen> experiencias =
                repo.findExperienciasPorCategoria(categoriaId);

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


    /*@GetMapping("/{id}")
    public PeliculaDetalle show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            return repo.findDetalle(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }*/

