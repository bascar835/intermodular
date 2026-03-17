package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.ExperienciaRepository;
@RestController
@RequestMapping("/api/admin/categorias")
public class ExperienciaAdminController {
	private final DataSource ds;

    public ExperienciaAdminController(DataSource ds) {
    	this.ds = ds;
    }
    
    @GetMapping
    public List<Experiencia> index() throws SQLException {
    	try (Connection con = ds.getConnection()) {
    	    ExperienciaRepository repo = new ExperienciaRepository(con);
    	    return repo.findAll();
    	 } catch (SQLException e) {
    	        throw new DataAccessException(e);
    	 }
    }
    
    @GetMapping("/{id}")
    public Experiencia show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            return repo.find(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PostMapping
    public Experiencia store(@RequestBody Experiencia experiencia) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            repo.insert(experiencia);
            return experiencia;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PutMapping("/{id}")
    public Experiencia update(@PathVariable int id, @RequestBody Experiencia experiencia) {
    	System.out.println(experiencia);
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            experiencia.setId(id);
            repo.update(experiencia);
            return experiencia;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @DeleteMapping("/{id}")
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            ExperienciaRepository repo = new ExperienciaRepository(con);
            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
