package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.entity.User;
import com.example.experiencias.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 🔹 Crea el repositorio con la conexión
    private UserRepository getRepository() {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/experiencias", // Cambia si tu BD tiene otro puerto/nombre
                "postgres",                                      // Usuario
                "tu_password"                                    // Password
            );
            return new UserRepository(con);
        } catch (Exception e) {
            throw new RuntimeException("Error conectando a la BD", e);
        }
    }

    // 🔹 GET /api/users → lista todos los usuarios
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return getRepository().findAllResponses();
    }

    // 🔹 GET /api/users/{id} → usuario por id
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        Optional<UserResponse> user = getRepository().findResponseById(id);
        return user.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 🔹 POST /api/users → crear usuario
    @PostMapping
    public UserResponse createUser(@RequestBody User user) {
        // ⚠️ Si quieres, aquí podrías añadir hash de contraseña
        user.setFechaCreacion(LocalDateTime.now());
        getRepository().insert(user);
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    // 🔹 PUT /api/users/{id} → actualizar usuario
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

    // 🔹 DELETE /api/users/{id} → eliminar usuario
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        boolean deleted = getRepository().delete(id);
        return deleted ? "Usuario eliminado" : "No se pudo eliminar el usuario";
    }
}