package com.example.experiencias.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.CategoriaRequest;
import com.example.experiencias.dto.CategoriaResumen;
import com.example.experiencias.entity.Categoria;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.helper.StorageHelper;
import com.example.experiencias.repository.CategoriaRepository;
import com.example.experiencias.validation.ImageValidator;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaAdminController {

    private final DataSource ds;
    private final StorageHelper storage;

    public CategoriaAdminController(DataSource ds, StorageHelper storage) {
        this.ds = ds;
        this.storage = storage;
    }

    // GET /api/admin/categorias — todas (incluye inactivas para el admin)
    @GetMapping
    public List<CategoriaResumen> index() {
        try (Connection con = ds.getConnection()) {
            return new CategoriaRepository(con).findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/categorias/{id}
    @GetMapping("/{id}")
    public Categoria show(@PathVariable("id") int id) {
        try (Connection con = ds.getConnection()) {
            Categoria cat = new CategoriaRepository(con).find(id);
            if (cat == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
            return cat;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/categorias
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria store(
            @Valid @ModelAttribute CategoriaRequest req,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            ImageValidator.validate(imagen);
            try { imagenUrl = storage.save(imagen, "categorias"); }
            catch (IOException e) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen"); }
        }

        // activo: true por defecto si no se envía
        boolean activo = req.activo() == null || req.activo();

        try (Connection con = ds.getConnection()) {
            Categoria cat = new Categoria(null, req.nombre(), req.descripcion(), imagenUrl, activo);
            new CategoriaRepository(con).insert(cat);
            return cat;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/categorias/{id}
    @PutMapping("/{id}")
    public Categoria update(
            @PathVariable("id") int id,
            @Valid @ModelAttribute CategoriaRequest req,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            Categoria existing = repo.find(id);
            if (existing == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");

            String imagenUrl = existing.getImagenUrl();
            if (imagen != null && !imagen.isEmpty()) {
                ImageValidator.validate(imagen);
                try {
                    if (imagenUrl != null) storage.deleteByUrl(imagenUrl);
                    imagenUrl = storage.save(imagen, "categorias");
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
                }
            }

            boolean activo = req.activo() == null ? existing.isActivo() : req.activo();
            Categoria cat = new Categoria(id, req.nombre(), req.descripcion(), imagenUrl, activo);
            repo.update(cat);
            return cat;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PATCH /api/admin/categorias/{id}/toggle — activa o desactiva sin tocar el resto
    @PatchMapping("/{id}/toggle")
    public Categoria toggle(@PathVariable("id") int id) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            Categoria cat = repo.find(id);
            if (cat == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
            cat.setActivo(!cat.isActivo());
            repo.update(cat);
            return cat;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/categorias/{id}
    @DeleteMapping("/{id}")
    public void destroy(@PathVariable("id") int id) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            Categoria existing = repo.find(id);
            if (existing != null && existing.getImagenUrl() != null) {
                storage.deleteByUrl(existing.getImagenUrl());
            }
            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
