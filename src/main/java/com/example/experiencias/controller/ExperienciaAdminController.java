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

import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.helper.StorageHelper;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.validation.ImageValidator;

@RestController
@RequestMapping("/api/admin/experiencias")
public class ExperienciaAdminController {

    private final DataSource ds;
    private final StorageHelper storage;

    public ExperienciaAdminController(DataSource ds, StorageHelper storage) {
        this.ds = ds;
        this.storage = storage;
    }

    // GET /api/admin/experiencias
    @GetMapping
    public List<Experiencia> index() {
        try (Connection con = ds.getConnection()) {
            return new ExperienciaRepository(con).findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/experiencias/{id}
    @GetMapping("/{id}")
    public Experiencia show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            return new ExperienciaRepository(con).find(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/experiencias  (multipart/form-data)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Experiencia store(
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("ubicacion") String ubicacion,
            @RequestParam("duracion_horas") int duracionHoras,
            @RequestParam("categoria_id") int categoriaId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        String imagenUrl = null;

        if (imagen != null && !imagen.isEmpty()) {
            ImageValidator.validate(imagen);
            try {
                imagenUrl = storage.save(imagen, "experiencias");
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
            }
        }

        try (Connection con = ds.getConnection()) {
            Experiencia exp = new Experiencia(null, titulo, descripcion, precio,
                    ubicacion, duracionHoras, categoriaId, null, imagenUrl);
            new ExperienciaRepository(con).insert(exp);
            return exp;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/experiencias/{id}  (multipart/form-data)
    @PutMapping("/{id}")
    public Experiencia update(
            @PathVariable int id,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("ubicacion") String ubicacion,
            @RequestParam("duracion_horas") int duracionHoras,
            @RequestParam("categoria_id") int categoriaId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try (Connection con = ds.getConnection()) {

            ExperienciaRepository repo = new ExperienciaRepository(con);
            Experiencia existing = repo.find(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiencia no encontrada");
            }

            String imagenUrl = existing.getImagenUrl(); // conservar la anterior por defecto

            if (imagen != null && !imagen.isEmpty()) {
                ImageValidator.validate(imagen);
                try {
                    // borrar la imagen anterior si existía
                    if (imagenUrl != null) {
                        storage.deleteByUrl(imagenUrl);
                    }
                    imagenUrl = storage.save(imagen, "experiencias");
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen");
                }
            }

            Experiencia exp = new Experiencia(id, titulo, descripcion, precio,
                    ubicacion, duracionHoras, categoriaId, existing.getFecha_creacion(), imagenUrl);
            repo.update(exp);
            return exp;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/experiencias/{id}
    @DeleteMapping("/{id}")
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            Experiencia existing = repo.find(id);
            if (existing != null && existing.getImagenUrl() != null) {
                storage.deleteByUrl(existing.getImagenUrl());
            }
            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
