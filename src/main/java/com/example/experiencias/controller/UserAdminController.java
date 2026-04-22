package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.experiencias.dto.UserRequest;
import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final DataSource ds;
    private final PasswordEncoder passwordEncoder;

    public UserAdminController(DataSource ds, PasswordEncoder passwordEncoder) {
        this.ds = ds;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/admin/users
    @GetMapping
    public List<UserResponse> index() {
        try (Connection con = ds.getConnection()) {
            return new UserRepository(con).findAllResponses();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/admin/users/{id}
    @GetMapping("/{id}")
    public UserResponse show(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            return new UserRepository(con).findResponseById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/admin/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse store(@Valid @RequestBody UserRequest req) {
        try (Connection con = ds.getConnection()) {
            User user = new User();
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(passwordEncoder.encode(req.password()));
            user.setRole(req.role() != null ? req.role() : "ROLE_USER");
            user.setFechaCreacion(LocalDateTime.now());

            new UserRepository(con).insert(user);

            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/users/{id}
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable int id, @Valid @RequestBody UserRequest req) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);

            // Verificar que existe y obtener la fecha original para no sobreescribirla
            User existing = repo.find(id);
            if (existing == null) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
            }

            User user = new User();
            user.setId(id);
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(passwordEncoder.encode(req.password()));
            user.setRole(req.role() != null ? req.role() : "ROLE_USER");
            user.setFechaCreacion(existing.getFechaCreacion()); // preservar fecha original

            repo.update(user);

            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            boolean deleted = new UserRepository(con).delete(id);
            if (!deleted) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
