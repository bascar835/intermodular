package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ReservaRepository;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaAdminController {

    // Valores válidos del ENUM estado_reserva en la BD
    private static final Set<String> ESTADOS_VALIDOS = Set.of("pendiente", "confirmada", "cancelada");

    private final DataSource ds;

    public ReservaAdminController(DataSource ds) {
        this.ds = ds;
    }

    @GetMapping
    public List<Reserva> index() throws SQLException {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            return repo.findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @GetMapping("/{id}")
    public Reserva show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            return repo.find(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PostMapping
    public Reserva store(@RequestBody Reserva reserva) {
        validarEstado(reserva);
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            repo.insert(reserva);
            return reserva;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PutMapping("/{id}")
    public Reserva update(@PathVariable int id, @RequestBody Reserva reserva) {
        validarEstado(reserva);
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            reserva.setId(id);
            repo.update(reserva);
            return reserva;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @DeleteMapping("/{id}")
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // CORREGIDO: normaliza y valida el estado contra el ENUM de la BD
    private void validarEstado(Reserva reserva) {
        String estadoNormalizado = reserva.getEstado() != null
            ? reserva.getEstado().toLowerCase().trim()
            : "pendiente";

        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Estado inválido. Valores permitidos: pendiente, confirmada, cancelada");
        }
        reserva.setEstado(estadoNormalizado);
    }
}
