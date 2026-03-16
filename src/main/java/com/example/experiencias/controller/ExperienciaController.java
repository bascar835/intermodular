package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.dto.ExperienciaResumen;
import com.example.experiencias.dto.PeliculaDetalle;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.repository.ExperienciaRepository;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaController {
	private final DataSource ds;
    public ExperienciaController(DataSource ds) {
        this.ds = ds;
    }
    
    @GetMapping
    public List<ExperienciaResumen> index() {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            return repo.findResumen();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
    /*
    @GetMapping("/{id}")
    public PeliculaDetalle show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            return repo.findDetalle(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
    */
}
