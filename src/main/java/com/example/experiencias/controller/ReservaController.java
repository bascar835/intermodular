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
@RequestMapping("/api/reservas")
public class ReservaController {

    private final DataSource ds;

    public ReservaController(DataSource ds) {
        this.ds = ds;
    }

    @GetMapping
    public List<Reserva> index() {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
            return repo.findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PostMapping
    public Reserva create(@RequestBody Reserva r)  {
        try (Connection con = ds.getConnection()) {
            ReservaRepository repo = new ReservaRepository(con);
             repo.insert(r);
             return r;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}

