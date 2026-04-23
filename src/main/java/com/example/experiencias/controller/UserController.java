package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DataSource ds;
    private final PasswordEncoder passwordEncoder;

    public UserController(DataSource ds, PasswordEncoder passwordEncoder) {
        this.ds = ds;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/users → lista todos los usuarios
    @GetMapping
    public List<UserResponse> getAllUsers() {
        try (Connection con = ds.getConnection()) {
            return new UserRepository(con).findAllResponses();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // GET /api/users/{id} → usuario por id
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            Optional<UserResponse> user = new UserRepository(con).findResponseById(id);
            return user.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // POST /api/users → crear usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody User user) {
        try (Connection con = ds.getConnection()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setFechaCreacion(LocalDateTime.now());
            new UserRepository(con).insert(user);
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

    // PUT /api/users/{id} → actualizar usuario
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable int id, @RequestBody User user) {
        try (Connection con = ds.getConnection()) {
            user.setId(id);
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            new UserRepository(con).update(user);
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

    // DELETE /api/users/{id} → eliminar usuario
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        try (Connection con = ds.getConnection()) {
            boolean deleted = new UserRepository(con).delete(id);
            return deleted ? "Usuario eliminado" : "No se pudo eliminar el usuario";
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
