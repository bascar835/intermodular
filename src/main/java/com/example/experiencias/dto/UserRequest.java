package com.example.experiencias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @Pattern(
        regexp = "ROLE_USER|ROLE_ADMIN",
        message = "Rol inválido. Valores permitidos: ROLE_USER, ROLE_ADMIN"
    )
    String role

) {}
