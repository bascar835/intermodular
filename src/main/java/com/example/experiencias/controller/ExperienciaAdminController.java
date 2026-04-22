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

import com.example.experiencias.dto.ExperienciaDetalle;
import com.example.experiencias.dto.ExperienciaRequest;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.dto.ImagenResponse;
import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.entity.ExperienciaImagen;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.helper.StorageHelper;
import com.example.experiencias.repository.ExperienciaImagenRepository;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.validation.ImageValidator;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/experiencias")
public class ExperienciaAdminController extends BaseController {

    private final StorageHelper storage;

    public ExperienciaAdminController(DataSource ds, StorageHelper storage) {
        super(ds);
        this.storage = storage;
    }

    // GET /api/admin/experiencias
    @GetMapping
    public List<ExperienciaResumen> index() {
        try (Connection con = ds.getConnection()) {
            return new ExperienciaRepository(con).findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/experiencias/{id}
    @GetMapping("/{id}")
    public ExperienciaDetalle show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaDetalle detalle = new ExperienciaRepository(con).findDetalle(id);
            if (detalle == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiencia no encontrada");
            }
            return detalle;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/experiencias  (multipart/form-data)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Experiencia store(
            @Valid @ModelAttribute ExperienciaRequest req,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try (Connection con = ds.getConnection()) {
            Experiencia exp = new Experiencia(
                    null,
                    req.titulo(), req.descripcion(), req.precio(),
                    req.ubicacion(), req.duracion_horas(), req.categoria_id(),
                    null);
            new ExperienciaRepository(con).insert(exp);

            if (imagen != null && !imagen.isEmpty()) {
                ImageValidator.validate(imagen);
                String url = storage.save(imagen, "experiencias");
                new ExperienciaImagenRepository(con).insert(new ExperienciaImagen(null, exp.getId(), url));
            }

            return exp;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen");
        }
    }

    // PUT /api/admin/experiencias/{id}  (multipart/form-data)
    @PutMapping("/{id}")
    public Experiencia update(
            @PathVariable int id,
            @Valid @ModelAttribute ExperienciaRequest req,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            Experiencia existing = repo.findOrThrow(id);

            Experiencia exp = new Experiencia(
                    id,
                    req.titulo(), req.descripcion(), req.precio(),
                    req.ubicacion(), req.duracion_horas(), req.categoria_id(),
                    existing.getFecha_creacion());
            repo.update(exp);

            if (imagen != null && !imagen.isEmpty()) {
                ImageValidator.validate(imagen);
                String url = storage.save(imagen, "experiencias");
                new ExperienciaImagenRepository(con).insert(new ExperienciaImagen(null, id, url));
            }

            return exp;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen");
        }
    }

    // DELETE /api/admin/experiencias/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            repo.findOrThrow(id);

            // Usar findByExperienciaId en lugar de findAll()+filter
            List<ImagenResponse> imagenes = new ExperienciaImagenRepository(con)
                    .findByExperienciaId(id);

            for (ImagenResponse img : imagenes) {
                storage.deleteByUrl(img.url());
            }

            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
