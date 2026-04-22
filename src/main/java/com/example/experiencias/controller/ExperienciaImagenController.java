package com.example.experiencias.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.entity.ExperienciaImagen;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.helper.StorageHelper;
import com.example.experiencias.repository.ExperienciaImagenRepository;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.validation.ImageValidator;

@RestController
@RequestMapping("/api/admin/experiencias/{experienciaId}/imagenes")
public class ExperienciaImagenController extends BaseController {

    private final StorageHelper storage;

    public ExperienciaImagenController(DataSource ds, StorageHelper storage) {
        super(ds);
        this.storage = storage;
    }

    // POST /api/admin/experiencias/{experienciaId}/imagenes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienciaImagen store(@PathVariable int experienciaId,
                                   @RequestParam("file") MultipartFile file) {

        try (Connection con = ds.getConnection()) {

            // Validar que la experiencia existe
            new ExperienciaRepository(con).findOrThrow(experienciaId);

            // Validar el archivo (tipo, tamaño)
            ImageValidator.validate(file);

            // Guardar en disco → devuelve la URL pública
            String url = storage.save(file, "experiencias");

            // Persistir la relación en BD
            ExperienciaImagenRepository repo = new ExperienciaImagenRepository(con);
            ExperienciaImagen img = new ExperienciaImagen(null, experienciaId, url);
            return repo.insert(img);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar la imagen");
        }
    }

    // DELETE /api/admin/experiencias/{experienciaId}/imagenes/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {

        try (Connection con = ds.getConnection()) {

            ExperienciaImagenRepository repo = new ExperienciaImagenRepository(con);

            // Obtener la imagen para recuperar la URL antes de borrarla
            ExperienciaImagen img = repo.findOrThrow(id);

            // Eliminar el registro en BD
            repo.delete(id);

            // Eliminar el archivo físico del disco
            storage.deleteByUrl(img.getUrl());

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
