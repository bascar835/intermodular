package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.ReservaResumen;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ReservaRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    // Valores válidos del ENUM estado_reserva en la BD
    private static final Set<String> ESTADOS_VALIDOS = Set.of("pendiente", "confirmada", "cancelada");

    private final DataSource ds;

    public ReservaController(DataSource ds) {
        this.ds = ds;
    }

    // GET /api/reservas → solo ADMIN (protegido por RoleInterceptor vía /api/admin/**)
    @GetMapping
    public List<Reserva> index() {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            return repo.findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
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
            ReservaRepository repo = new ReservaRepository(con);
            return repo.findByUsuario(userId);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/reservas → crear reserva (requiere sesión)
    @PostMapping
    public Reserva create(@RequestBody Reserva r, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        // Seguridad: forzar usuario desde sesión del servidor, nunca del cliente
        Integer userId = (Integer) session.getAttribute("userId");
        r.setUsuario_id(userId);

        // Validar experiencia_id
        if (r.getExperiencia_id() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "experiencia_id inválido");
        }

        // Validar numero_personas
        if (r.getNumero_personas() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numero_personas debe ser al menos 1");
        }

        // Validar precio_total
        if (r.getPrecio_total() == null || r.getPrecio_total() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "precio_total inválido");
        }

        // Validar fecha_reserva
        if (r.getFecha_reserva() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecha_reserva es obligatoria");
        }

        // CORREGIDO: validar estado contra el ENUM de la BD (normalizar a minúsculas)
        String estadoNormalizado = r.getEstado() != null ? r.getEstado().toLowerCase().trim() : "pendiente";
        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Estado inválido. Valores permitidos: pendiente, confirmada, cancelada");
        }
        r.setEstado(estadoNormalizado);

        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            repo.insert(r);
            return r;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
