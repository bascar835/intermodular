package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.ExperienciasResumen;
import com.example.experiencias.dto.PeliculaDetalle;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ExperienciasRepository;

@RestController
@RequestMapping("/api/categorias")
public class ExperienciaController {
	private final DataSource ds;
    public ExperienciaController(DataSource ds) {
        this.ds = ds;
    }
    
    @GetMapping
    public List<ExperienciasResumen> index() {
        try (Connection con = ds.getConnection()) {
            ExperienciasRepository repo = new ExperienciasRepository(con);
            return repo.findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
    @GetMapping("/{id}")
    public PeliculaDetalle show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciasRepository repo = new ExperienciasRepository(con);
            return repo.findDetalle(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
