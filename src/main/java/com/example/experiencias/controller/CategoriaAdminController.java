package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.entity.Categoria;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.CategoriaRepository;

@RestController
@RequestMapping("/api/admin/categorias")
public class CategoriaAdminController {
	private final DataSource ds;

    public CategoriaAdminController(DataSource ds) {
    	this.ds = ds;
    }
    
    @GetMapping
    public List<Categoria> index() throws SQLException {
    	try (Connection con = ds.getConnection()) {
    	    CategoriaRepository repo = new CategoriaRepository(con);
    	    return repo.findAll();
    	 } catch (SQLException e) {
    	        throw new DataAccessException(e);
    	 }
    }
    
    @GetMapping("/{id}")
    public Categoria show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            return repo.find(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PostMapping
    public Categoria store(@RequestBody Categoria categoria) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            repo.insert(categoria);
            return categoria;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @PutMapping("/{id}")
    public Categoria update(@PathVariable int id, @RequestBody Categoria categoria) {
    	System.out.println(categoria);
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            categoria.setId(id);
            repo.update(categoria);
            return categoria;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @DeleteMapping("/{id}")
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            CategoriaRepository repo = new CategoriaRepository(con);
            repo.delete(id);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
