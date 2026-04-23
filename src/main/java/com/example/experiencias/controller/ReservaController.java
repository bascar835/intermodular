package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.ReservaRequest;
import com.example.experiencias.dto.ReservaResumen;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ReservaRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final DataSource ds;

    public ReservaController(DataSource ds) {
        this.ds = ds;
    }

    // GET /api/reservas/mis-reservas → reservas del usuario en sesión
    @GetMapping("/mis-reservas")
    public List<ReservaResumen> misReservas(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        Integer userId = (Integer) session.getAttribute("userId");

        try (Connection con = ds.getConnection()) {
            return new ReservaRepository(con).findByUsuario(userId);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/reservas → crear reserva (requiere sesión)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva create(@Valid @RequestBody ReservaRequest req, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        // Seguridad: usuario siempre desde sesión del servidor, nunca del cliente
        Integer userId = (Integer) session.getAttribute("userId");

        // Estado por defecto si no se envía
        String estado = (req.estado() != null && !req.estado().isBlank())
                ? req.estado().toLowerCase().trim()
                : "pendiente";

        Reserva r = new Reserva(
                null,
                userId,
                req.experiencia_id(),
                req.fecha_reserva(),
                req.numero_personas(),
                req.precio_total(),
                estado);

        try (Connection con = ds.getConnection()) {
            new ReservaRepository(con).insert(r);
            return r;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
