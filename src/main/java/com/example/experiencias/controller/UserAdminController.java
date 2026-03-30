package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final DataSource ds;

    public UserAdminController(DataSource ds) {
        this.ds = ds;
    }

    // GET /api/admin/users → lista todos los usuarios
    @GetMapping
    public List<UserResponse> index() {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);
            return repo.findAllResponses();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/users/{id} → detalle de un usuario
    @GetMapping("/{id}")
    public UserResponse show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);
            return repo.findResponseById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/users → crear usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse store(@RequestBody User user) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);
            user.setFechaCreacion(LocalDateTime.now());
            repo.insert(user);
            return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
            );
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/users/{id} → actualizar usuario
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable int id, @RequestBody User user) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);
            user.setId(id);
            repo.update(user);
            return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
            );
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/users/{id} → eliminar usuario
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);
            boolean deleted = repo.delete(id);
            if (!deleted) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
