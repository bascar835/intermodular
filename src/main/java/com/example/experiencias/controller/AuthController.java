package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.experiencias.dto.UserResponse;
import com.example.experiencias.dto.auth.LoginRequest;
import com.example.experiencias.dto.auth.RegisterRequest;
import com.example.experiencias.entity.User;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final DataSource ds;
    private final BCryptPasswordEncoder encoder;

    public AuthController(DataSource ds) {
        this.ds = ds;
        this.encoder = new BCryptPasswordEncoder();
    }

    // -------------------------
    // POST /api/auth/register
    // -------------------------
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest body,
                                 HttpServletRequest request) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);

            // Comprobar que el email no está ya en uso
            if (repo.findByEmail(body.email()).isPresent()) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El email ya está registrado"
                );
            }

            // Crear el usuario con la contraseña hasheada
            User user = new User();
            user.setName(body.name());
            user.setEmail(body.email());
            user.setPassword(encoder.encode(body.password()));
            user.setRole("ROLE_USER");
            user.setFechaCreacion(LocalDateTime.now());

            repo.insert(user);

            // Abrir sesión automáticamente tras el registro
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());

            return new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole()
            );

        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // -------------------------
    // POST /api/auth/login
    // -------------------------
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest body,
                              HttpServletRequest request) {
        try (Connection con = ds.getConnection()) {
            UserRepository repo = new UserRepository(con);

            // Buscar el usuario por email
            User user = repo.findByEmail(body.email())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"
                ));

            // Verificar la contraseña contra el hash almacenado
            if (!encoder.matches(body.password(), user.getPassword())) {
                throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"
                );
            }

            // Escribir en sesión — esto es lo que leen AuthInterceptor y RoleInterceptor
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());

            return new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole()
            );

        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // -------------------------
    // POST /api/auth/logout
    // -------------------------
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
