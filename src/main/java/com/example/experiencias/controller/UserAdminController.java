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
            UserRepository repo = new UserRepository(con);

            if (repo.emailExists(req.email())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo ya está en uso por otro usuario");
            }

            User user = new User();
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(passwordEncoder.encode(req.password()));
            user.setRole(req.role() != null ? req.role() : "ROLE_USER");
            user.setFechaCreacion(LocalDateTime.now());
            user.setDeleted(false);

            repo.insert(user);

            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // PUT /api/admin/users/{id}
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable int id, @Valid @RequestBody UserRequest req) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);

            User existing = repo.find(id);
            if (existing == null) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
            }

            if (repo.emailExistsForOtherUser(req.email(), id)) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo está duplicado: ya pertenece a otro usuario");
            }

            User user = new User();
            user.setId(id);
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(passwordEncoder.encode(req.password()));
            user.setRole(req.role() != null ? req.role() : "ROLE_USER");
            user.setFechaCreacion(existing.getFechaCreacion());
            user.setDeleted(existing.getDeleted() != null ? existing.getDeleted() : false);

            repo.update(user);

            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/{id}")
    public String destroy(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);

            User existing = repo.find(id);
            if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
            }

            if ("ROLE_ADMIN".equals(existing.getRole()) && repo.countAdmins() <= 1) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el último administrador del sistema");
            }

            // Eliminar reservas del usuario antes de eliminarlo
            repo.eliminarReservas(id);

            boolean deleted = repo.softDelete(id);
            if (!deleted) {
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo eliminar el usuario");
            }

            return "Usuario y sus reservas eliminados correctamente";
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}