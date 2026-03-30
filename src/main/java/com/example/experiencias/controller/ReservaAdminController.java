package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ReservaRepository;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaAdminController {

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
        System.out.println(reserva);
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
}
