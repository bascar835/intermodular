package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DataSource ds;

    public UserController(DataSource ds) {
        this.ds = ds;
    }

    private UserRepository getRepository() {
        try {
            Connection con = ds.getConnection();
            return new UserRepository(con);
        } catch (SQLException e) {
            throw new RuntimeException("Error conectando a la BD", e);
        }
    }

    // GET /api/users → lista todos los usuarios
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return getRepository().findAllResponses();
    }

    // GET /api/users/{id} → usuario por id
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        Optional<UserResponse> user = getRepository().findResponseById(id);
        return user.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // POST /api/users → crear usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody User user) {
        user.setFechaCreacion(LocalDateTime.now());
        getRepository().insert(user);
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    // PUT /api/users/{id} → actualizar usuario
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        getRepository().update(user);
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    // DELETE /api/users/{id} → eliminar usuario
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        boolean deleted = getRepository().delete(id);
        return deleted ? "Usuario eliminado" : "No se pudo eliminar el usuario";
    }
}
