package com.example.experiencias.dto.auth;

public record RegisterRequest(
	String name, 
	String email, 
	String password
) {}
