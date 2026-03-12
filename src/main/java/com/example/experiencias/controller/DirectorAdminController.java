package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.entity.Director;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.DirectorRepository;

@RestController
@RequestMapping("/api/admin/directores")
public class DirectorAdminController {
	private final DataSource ds;
	
    public DirectorAdminController(DataSource ds) {
        this.ds = ds;
    }
    
    @GetMapping
    public List<Director> index() {
        try (Connection con = ds.getConnection()) {
            DirectorRepository repo = new DirectorRepository(con);
            return repo.findAll();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
