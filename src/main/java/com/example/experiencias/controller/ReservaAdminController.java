package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.ReservaAdminRequest;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ReservaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaAdminController {

    private final DataSource ds;

    public ReservaAdminController(DataSource ds) {
        this.ds = ds;
    }

    // GET /api/admin/reservas
    @GetMapping
    public List<Reserva> index() {
        try (Connection con = ds.getConnection()) {
            return new ReservaRepository(con).findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/reservas/{id}
    @GetMapping("/{id}")
    public Reserva show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            Reserva r = new ReservaRepository(con).find(id);
            if (r == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
            }
            return r;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/reservas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva store(@Valid @RequestBody ReservaAdminRequest req) {
        String estado = (req.estado() != null && !req.estado().isBlank())
                ? req.estado().toLowerCase().trim()
                : "pendiente";

        Reserva reserva = new Reserva(
                null,
                req.usuario_id(),       // ← ahora sí viene del formulario
                req.experiencia_id(),
                req.fecha_reserva(),
                req.numero_personas(),
                req.precio_total(),
                estado);

        try (Connection con = ds.getConnection()) {
            new ReservaRepository(con).insert(reserva);
            return reserva;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/reservas/{id}
    @PutMapping("/{id}")
    public Reserva update(@PathVariable int id, @Valid @RequestBody ReservaAdminRequest req) {
        String estado = (req.estado() != null && !req.estado().isBlank())
                ? req.estado().toLowerCase().trim()
                : "pendiente";

        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);

            Reserva existing = repo.find(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
            }

            Reserva reserva = new Reserva(
                    id,
                    req.usuario_id(),   // ← el admin puede cambiar el usuario también
                    req.experiencia_id(),
                    req.fecha_reserva(),
                    req.numero_personas(),
                    req.precio_total(),
                    estado);

            repo.update(reserva);
            return reserva;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/reservas/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            new ReservaRepository(con).delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
