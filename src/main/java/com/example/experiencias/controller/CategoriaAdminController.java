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

import com.example.experiencias.entity.Categoria;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.helper.StorageHelper;
import com.example.experiencias.repository.CategoriaRepository;
import com.example.experiencias.validation.ImageValidator;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaAdminController {

    private final DataSource ds;
    private final StorageHelper storage;

    public CategoriaAdminController(DataSource ds, StorageHelper storage) {
        this.ds = ds;
        this.storage = storage;
    }

    // GET /api/admin/categorias
    @GetMapping
    public List<Categoria> index() {
        try (Connection con = ds.getConnection()) {
            return new CategoriaRepository(con).findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/categorias/{id}
    @GetMapping("/{id}")
    public Categoria show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            return new CategoriaRepository(con).find(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/categorias  (multipart/form-data)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria store(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        String imagenUrl = null;

        if (imagen != null && !imagen.isEmpty()) {
            ImageValidator.validate(imagen);
            try {
                imagenUrl = storage.save(imagen, "categorias");
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
            }
        }

        try (Connection con = ds.getConnection()) {
            Categoria cat = new Categoria(null, nombre, descripcion, imagenUrl);
            new CategoriaRepository(con).insert(cat);
            return cat;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/categorias/{id}  (multipart/form-data)
    @PutMapping("/{id}")
    public Categoria update(
            @PathVariable int id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try (Connection con = ds.getConnection()) {

            CategoriaRepository repo = new CategoriaRepository(con);
            Categoria existing = repo.find(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
            }

            String imagenUrl = existing.getImagenUrl(); // conservar la anterior por defecto

            if (imagen != null && !imagen.isEmpty()) {
                ImageValidator.validate(imagen);
                try {
                    if (imagenUrl != null) {
                        storage.deleteByUrl(imagenUrl);
                    }
                    imagenUrl = storage.save(imagen, "categorias");
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
                }
            }

            Categoria cat = new Categoria(id, nombre, descripcion, imagenUrl);
            repo.update(cat);
            return cat;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/categorias/{id}
    @DeleteMapping("/{id}")
    public void destroy(@PathVariable int id) {
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
